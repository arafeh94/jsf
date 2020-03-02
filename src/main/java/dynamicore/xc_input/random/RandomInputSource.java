package dynamicore.xc_input.random;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.bll.RelationNodeBll;
import com.arafeh.jsf.core.utils.TextGenerator;
import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.NodeType;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.Static_Settings;
import dynamicore.input.*;
import dynamicore.input.middlewares.DataInputMiddleware;
import dynamicore.input.node.InputNode;

import java.util.Date;
import java.util.List;

import static com.arafeh.jsf.core.utils.Extensions.intVal;

public class RandomInputSource extends DataInputBase {

    private RandomInputResources.RandomInputNode graph;
    private int seed = 99;
    private int maxDepth = 3;

    @Override
    public void init(Project project, NodeBll nodes, GraphNodeBll graphNodes,
                     ProjectBll projectBll, RelationNodeBll relationNodeBll, List<DataInputMiddleware> middlewares) throws DataSourceNotRegisteredException {
        super.init(project, nodes, graphNodes, projectBll, relationNodeBll, middlewares);
        ProjectDataSource ds = project.getDataSource();
        ds.settingsOf(Static_Settings.RandomInputSourceSettings.SEED)
                .stream()
                .findFirst()
                .ifPresent(s -> seed = intVal(s.getValue()));
        ds.settingsOf(Static_Settings.RandomInputSourceSettings.MAX_DEPTH)
                .stream()
                .findFirst()
                .ifPresent(s -> maxDepth = intVal(s.getValue()));
        this.graph = RandomInputResources.graph(seed, maxDepth);
    }

    @Override
    public void scan(InputNode inputNode) {
        if (inputNode.location().getDepth() == 0) {
            for (RandomInputResources.RandomInputNode randomInputNode : graph.linked) {
                inputNode.link(createInputNode(randomInputNode.id, NodeType.ACCOUNT, "", randomInputNode.depth, randomInputNode.pos));
            }
        } else {
            RandomInputResources.RandomInputNode g =
                    graph.navigate(inputNode.getId());
            for (RandomInputResources.RandomInputNode randomInputNode : g.linked) {
                inputNode.link(createInputNode(randomInputNode.id, NodeType.FOLLOWER, "", randomInputNode.depth, randomInputNode.pos));
            }
        }
    }

    @Override
    protected Node fetch(InputNode lastNode) {
        Node node = createNode(lastNode.getType());
        node.setId(lastNode.getId());
        node.setCreatedAt(new Date());
        node.setExpireAt(new Date(new Date().getTime() + 100000));
        node.store("Name", TextGenerator.getInstance().getName() + " " + TextGenerator.getInstance().getMiddle());
        node.store("AccountName", TextGenerator.getInstance().getScreenName());
        node.store("Email", TextGenerator.getInstance().getEmail());
        return node;
    }

    @Override
    public Class<? extends DataInputBase> describe() {
        return RandomInputSource.class;
    }

    @Override
    public DataSourceState getStats() {
        return null;
    }
}
