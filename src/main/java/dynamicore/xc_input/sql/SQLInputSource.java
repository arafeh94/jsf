package dynamicore.xc_input.sql;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.bll.RelationNodeBll;
import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.NodeType;
import com.arafeh.jsf.model.Project;
import dynamicore.Static_Settings;
import dynamicore.input.DataInputBase;
import dynamicore.input.DataSourceNotRegisteredException;
import dynamicore.input.DataSourceState;
import dynamicore.input.middlewares.DataInputMiddleware;
import dynamicore.input.node.InputLocation;
import dynamicore.input.node.InputNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.arafeh.jsf.core.utils.Extensions.sleep;


@SuppressWarnings("Duplicates")
public class SQLInputSource extends DataInputBase {
    public static int FETCH_REQ_COUNT = 0;
    public static int SCAN_REQ_COUNT = 0;
    private int maxLinkedNodes = -1;

    @Override
    public void init(Project project, NodeBll nodes, GraphNodeBll graphNodes, ProjectBll projectBll, RelationNodeBll relationNodeBll, List<DataInputMiddleware> middlewares) throws DataSourceNotRegisteredException {
        super.init(project, nodes, graphNodes, projectBll, relationNodeBll, middlewares);
        dataSource.settingsOf(Static_Settings.InputSourceSettings.MAX_LOADED_NODES)
                .stream().findFirst()
                .ifPresent(s -> maxLinkedNodes = s.asInt());
    }

    @Override
    public void scan(InputNode inputNode) {
        SCAN_REQ_COUNT++;
        if (inputNode.location().equals(InputLocation.START)) {
            List<String> startingIds = project.getDataSource().startingIdsAsList();
            int pos = 0;
            for (String startingId : startingIds) {
                InputNode iNode = createInputNode(Long.valueOf(startingId), NodeType.ACCOUNT, "node", 1, pos++);
                inputNode.link(iNode);
            }
        } else {
            ArrayList<Long> ids = SQLSourceDataProvider.getInstance().getFollowersIDs(inputNode.getId());
            int iterations = maxLinkedNodes != -1 && maxLinkedNodes <= ids.size() ? maxLinkedNodes : ids.size();
            int depth = inputNode.location().getDepth() + 1;
            for (int i = 0; i < iterations; i++) {
                InputNode iNode = createInputNode(ids.get(i), NodeType.FOLLOWER, "", depth, i);
                inputNode.link(iNode);
            }
            backup(ids);
        }
    }

    //with this method we can bulk get user information instead of getting
    // them one by one, se we reduce the request count from twitter and also
    // it's 100 time faster
    private void backup(ArrayList<Long> ids) {
        ArrayList<Long> dif = nodeBll.dif(ids);
        for (Long id : dif) {
            nodeBll.set(SQLSourceDataProvider.getInstance().fetch(id, createNode(NodeType.FOLLOWER)));
        }
    }

    @Override
    protected Node fetch(InputNode lastNode) {
        FETCH_REQ_COUNT++;
        if (lastNode.location().equals(InputLocation.START)) {
            Node node = createNode(lastNode.getType());
            node.setCreatedAt(new Date());
            node.setId(lastNode.getId());
            return node;
        } else {
            return SQLSourceDataProvider.getInstance().fetch(lastNode.getId(),
                    createNode(lastNode.getType()));
        }
    }

    @Override
    public DataSourceState getStats() {
        return new DataSourceState();
    }

    @Override
    public Class<? extends DataInputBase> describe() {
        return SQLInputSource.class;
    }

}
