package dynamicore.xc_middlewares.extentions;

import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.NodeType;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import com.google.gson.Gson;
import dynamicore.Static_Settings;
import dynamicore.input.DataInputBase;
import dynamicore.input.middlewares.AfterScanFilterMiddleware;
import dynamicore.input.node.InputNode;
import dynamicore.tools.BroadcastManager;
import dynamicore.xc_input.twitter.TwitterInputSource;
import dynamicore.xc_input.twitter.TwitterPool;
import dynamicore.xc_middlewares.MinFollowers;
import twitter4j.Status;


public class IncludeTweets extends AfterScanFilterMiddleware {
    private int maxTweetsNumber = 10;

    public IncludeTweets() {

    }

    @Override
    public Boolean accepted(Project project, ProjectDataSource dataSource, Node node, InputNode iNode, int currentLevel) {
        if (iNode.getType() != NodeType.FOLLOWER && iNode.getType() != NodeType.ACCOUNT) return null;
        if (!dataSource.getSource().equals(TwitterInputSource.class.getName())) return null;
        dataSource.settingsOf(Static_Settings.InputSourceSettings.INCLUDE_TWEETS)
                .stream().findFirst()
                .ifPresent(s -> maxTweetsNumber = s.asInt(0));
        if (maxTweetsNumber > 0) {
            int included = 0;
            for (Status status : TwitterPool.getInstance().getTweets(iNode.getId(), null)) {
                Node sts = TwitterInputSource.asNode(NodeType.POST, status);
                InputNode childTweet = DataInputBase.createInputNode(project, TwitterInputSource.class, status.getId(), NodeType.POST, new Gson().toJson(sts), iNode.location().getDepth() + 1, iNode.children().size());
                if (!NodeBll.getInstance().exists(NodeType.POST, sts.getId(), TwitterInputSource.class)) {
                    NodeBll.getInstance().set(sts);
                }
                iNode.link(childTweet);
                included++;
                if (included >= maxTweetsNumber) break;
            }
        }
        return null;
    }

    @Override
    public Class<? extends AfterScanFilterMiddleware> describe() {
        return IncludeTweets.class;
    }

    @Override
    public boolean ignoreIfDead() {
        return true;
    }

    @Override
    public boolean ignoreFirstLevel() {
        return false;
    }
}
