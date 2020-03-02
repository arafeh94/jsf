package dynamicore.input.node;

import com.arafeh.jsf.core.protocols.Action;
import com.arafeh.jsf.model.GraphNode;

import java.util.ArrayList;

import static com.arafeh.jsf.core.utils.Extensions.loop;

public class NodeUtils {
    public static String draw(InputNode root) {
        StringBuilder builder = new StringBuilder("nodes: ");
        root.each(node -> {
            builder.append("\n");
            loop(node.location().getDepth(), v -> builder.append("\t"));
            builder.append(node.id).append(":").append(node.location());
        });
        return builder.toString();
    }

    public static void each(GraphNode root, Action<GraphNode> action) {
        each(root, action, new ArrayList<>());
    }

    private static void each(GraphNode root, Action<GraphNode> action, ArrayList<GraphNode> executed) {
        action.run(root);
        for (GraphNode graphNode : root.getLinked()) {
            if (!executed.contains(graphNode)) {
                executed.add(graphNode);
                each(graphNode, action, executed);
            }
        }
    }
}
