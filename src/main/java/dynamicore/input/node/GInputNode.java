package dynamicore.input.node;

import com.arafeh.jsf.core.protocols.Action;
import com.arafeh.jsf.core.protocols.Function;
import com.arafeh.jsf.core.utils.Func;
import com.google.common.graph.Graph;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GInputNode extends BaseNode<GInputNode> {
    public static GInputNode create(MutableGraph<GInputNode> graph, long id, long projectId, String source, String json, int depth, int pos) {
        GInputNode node = new GInputNode(graph);
        node.setId(id);
        node.setProjectId(projectId);
        node.setDead(false);
        node.setSource(source);
        node.setJson(json);
        node.setInputLocation(new InputLocation(depth, pos));
        return node;
    }

    private MutableGraph<GInputNode> graph;

    private GInputNode(MutableGraph<GInputNode> graph) {
        this.graph = graph;
    }

    @Override
    public void link(GInputNode node) {
        graph.putEdge(this, node);
    }

    @Override
    public void linkAll(Collection<GInputNode> nodes) {
        for (GInputNode node : nodes) {
            link(node);
        }
    }

    public List<GInputNode> parents() {
        return new ArrayList<>(graph.predecessors(this));
    }

    @Override
    public GInputNode parent() {
        return parents().iterator().next();
    }

    public List<GInputNode> children() {
        return new ArrayList<>(graph.successors(this));
    }

    @Override
    public GInputNode child(int pos) {
        return null;
    }

    public List<GInputNode> siblings() {
        return new ArrayList<>(graph.adjacentNodes(this));
    }

    @Override
    public List<GInputNode> neighborhood() {
        return null;
    }

    @Override
    public GInputNode root() {
        for (GInputNode node : graph.nodes()) {
            if (node.location().equals(InputLocation.START)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public GInputNode navigate(long id) {
        for (GInputNode iNode : graph.nodes()) {
            if (iNode.getId() == id) {
                return iNode;
            }
        }
        return null;
    }

    @Override
    public boolean hasChildren() {
        return children().size() > 0;
    }

    @Override
    public boolean exists(GInputNode node) {
        return graph.nodes().contains(node);
    }

    @Override
    public void each(Action<GInputNode> action) {
        for (GInputNode node : graph.nodes()) {
            action.run(node);
        }
    }

    @Override
    public int size() {
        return graph.nodes().size();
    }

    @Override
    public int size(Function<Boolean, InputNode> where) {
        return graph.nodes().size();
    }

    @Override
    public String compositeFormula() {
        return "{id}:{projectId}:{source}";
    }
}
