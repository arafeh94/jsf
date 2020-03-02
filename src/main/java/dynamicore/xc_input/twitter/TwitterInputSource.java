package dynamicore.xc_input.twitter;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.bll.RelationNodeBll;
import com.arafeh.jsf.core.protocols.Action;
import com.arafeh.jsf.core.utils.Var;
import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.NodeType;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.service.LoggingService;
import com.google.gson.Gson;
import dynamicore.Static_Settings;
import dynamicore.input.DataInputBase;
import dynamicore.input.DataSourceNotRegisteredException;
import dynamicore.input.DataSourceState;
import dynamicore.input.middlewares.DataInputMiddleware;
import dynamicore.input.node.InputLocation;
import dynamicore.input.node.InputNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.arafeh.jsf.core.utils.Extensions.*;
import static com.arafeh.jsf.model.NodeType.*;

@SuppressWarnings("Duplicates")
public class TwitterInputSource extends DataInputBase {
    private int maxLoad = -1;
    private Gson gson;

    @Override
    public void init(Project project, NodeBll nodes, GraphNodeBll graphNodes, ProjectBll projectBll, RelationNodeBll relationNodeBll, List<DataInputMiddleware> middlewares) throws DataSourceNotRegisteredException {
        super.init(project, nodes, graphNodes, projectBll, relationNodeBll, middlewares);
        dataSource.settingsOf(Static_Settings.InputSourceSettings.MAX_LOADED_NODES)
                .stream().findFirst()
                .ifPresent(s -> maxLoad = s.asInt(-1));
        gson = new Gson();
    }

    @Override
    public void scan(InputNode inputNode) {
        if (inputNode.location().equals(InputLocation.START)) {
            List<String> startingIds = project.getDataSource().startingIdsAsList();
            //all the strings that don't contains # means they are accounts
            List<String> accounts = startingIds.stream().filter(id -> !id.startsWith("#"))
                    //remove the starting @ if exists from the account name
                    .map(account -> account.startsWith("@") ? account.replaceFirst("@", "") : account)
                    .collect(Collectors.toList());
            List<String> tags = startingIds.stream().filter(id -> id.startsWith("#")).collect(Collectors.toList());
            int pos = 0;
            for (Node node : lookupNames(accounts)) {
                InputNode iNode = createInputNode(node.getId(), NodeType.ACCOUNT, gson.toJson(node.getStore()), 1, pos++);
                inputNode.link(iNode);
            }
            for (String hashTag : tags) {
                InputNode iNode = createInputNode(random().UUIDLong(), NodeType.HASHTAG, hashTag, 1, pos++);
                inputNode.link(iNode);
            }
        } else {
            switch (inputNode.getType()) {
                case HASHTAG:
                    scanHashTag(inputNode);
                    break;
                case ACCOUNT:
                case FOLLOWER:
                    scanAccount(inputNode);
                    break;
                case POST:
                    scanTweet(inputNode);
                    break;
            }
        }
    }

    private void scanTweet(InputNode inputNode) {
        int pos = 0;
        for (Node node : getRetweets(inputNode.getId())) {
            InputNode iNode = createInputNode(node.getId(), NodeType.COMMENT, gson.toJson(node), inputNode.location().getDepth() + 1, pos++);
            inputNode.link(iNode);
        }
    }

    private void scanHashTag(InputNode inputNode) {
        int pos = 0;
        for (Node node : lookupHashtag(listOf(inputNode.getJson()))) {
            InputNode iNode = createInputNode(node.getId(), NodeType.POST, gson.toJson(node), inputNode.location().getDepth() + 1, pos++);
            inputNode.link(iNode);
        }
    }

    /**
     * return the ids of the follower of this account.
     * Additional note:
     * the number of nodes that are linked to this branch are limited.
     * for example if maxLoad is set to 500 and
     * the node has 5 million link (user->follower) instead of adding 5 million ids we add only
     * 500 in this case we're going to fetch only 500 later instead of 5 million
     *
     * @param inputNode
     */
    private void scanAccount(InputNode inputNode) {
        ArrayList<Long> ids = TwitterPool.getInstance().getFollowerIDs(inputNode.getId(), new TwitterExceptionHandler());

        int maxLinkedNode = maxLoad > 0 && maxLoad < ids.size() ? maxLoad : ids.size();
        int depth = inputNode.location().getDepth() + 1;
        for (int i = 0; i < maxLinkedNode; i++) {
            InputNode iNode = createInputNode(ids.get(i), NodeType.FOLLOWER, "", depth, i);
            inputNode.link(iNode);
        }
        backup(ids);
    }

    // with this method we can bulk get user information instead of getting
    // them one by one, se we reduce the request count from twitter and also
    // it's 100 time faster
    private void backup(ArrayList<Long> ids) {
        ArrayList<Long> dif = nodeBll.dif(ids);
        for (User user : TwitterPool.getInstance().lookupIds(dif, new TwitterExceptionHandler())) {
            nodeBll.set(asNode(FOLLOWER, user));
        }
    }

    @Override
    protected Node fetch(InputNode inputNode) {
        switch (inputNode.getType()) {
            case ACCOUNT:
            case FOLLOWER:
                Var<Node> node = new Var<>();
                TwitterPool.getInstance().lookup(inputNode.getId(), new TwitterExceptionHandler())
                        .ifPresent(user -> node.set(asNode(FOLLOWER, user)));

                return node.get();
            default:
                return nodeBll.get(inputNode.getId(), inputNode.getType().name(), describe().getSimpleName())
                        .orElse(null);
        }
    }

    @Override
    public DataSourceState getStats() {
        return new DataSourceState();
    }

    @Override
    public Class<? extends DataInputBase> describe() {
        return TwitterInputSource.class;
    }

    public ArrayList<Node> getRetweets(long id) {
        ArrayList<Node> list = new ArrayList<>();
        for (Status status : TwitterPool.getInstance().getRetweets(id, new TwitterExceptionHandler())) {
            Node node = asNode(POST, status);
            list.add(node);
            nodeBll.set(node);
        }
        return list;
    }

    public ArrayList<Node> lookupNames(List<String> names) {
        ArrayList<Node> list = new ArrayList<>();
        for (User user : TwitterPool.getInstance().lookupNames(names, new TwitterExceptionHandler())) {
            Node node = asNode(ACCOUNT, user);
            list.add(node);
            nodeBll.set(node);
        }
        return list;
    }


    private ArrayList<Node> lookupHashtag(List<String> tags) {
        ArrayList<Node> list = new ArrayList<>();
        for (Status status : TwitterPool.getInstance().query(tags, new TwitterExceptionHandler())) {
            Node node = asNode(POST, status);
            list.add(node);
            nodeBll.set(node);
        }
        return list;
    }

    public static Node asNode(NodeType type, Object twitterObject) {
        Node node = new Node(type, TwitterInputSource.class.getSimpleName());
        if (twitterObject instanceof User) {
            User user = (User) twitterObject;
            node.setId(user.getId());
            node.setType(type.name());
            node.store("Email", user.getEmail());
            node.store("AccountName", user.getScreenName());
            node.store("Name", user.getName());
            node.store("CreatedAt", user.getCreatedAt());
            node.store("Description", user.getDescription());
            node.store("Lang", user.getLang());
            node.store("Location", user.getLocation());
            node.store("FollowersCount", user.getFollowersCount());
            node.store("FriendsCount", user.getFriendsCount());
            node.store("Protected", String.valueOf(user.isProtected()));
            node.store("Verified", String.valueOf(user.isVerified()));
        } else if (twitterObject instanceof Status) {
            Status tweet = (Status) twitterObject;
            node.setType(type.name());
            node.setId(tweet.getId());
            node.setCreatedAt(tweet.getCreatedAt());
            node.store("Lang", tweet.getLang());
            node.store("Place", tweet.getPlace());
            node.store("RetweetCount", tweet.getRetweetCount());
            node.store("Text", tweet.getText());
            node.store("Scopes", tweet.getScopes());
            node.store("FavoriteCount", tweet.getFavoriteCount());
            node.store("HashTag", hashTags(tweet.getHashtagEntities()));
            node.store("Location", "");
            if (tweet.getGeoLocation() != null) {
                node.store("Location", tweet.getGeoLocation().getLatitude() + ":" + tweet.getGeoLocation().getLatitude());
            }
        }
        return node;
    }

    private static String hashTags(HashtagEntity[] hashtagEntities) {
        if (hashtagEntities == null) return "";
        StringBuilder hashTags = new StringBuilder();
        for (HashtagEntity hashtagEntity : hashtagEntities) {
            hashTags.append(hashtagEntity.getText()).append(";");
        }
        return hashTags.toString();
    }

    public class TwitterExceptionHandler implements Action<Exception> {

        @Override
        public void run(Exception e) {

        }
    }
}
