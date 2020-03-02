package dynamicore.input.node;

import com.arafeh.jsf.model.GraphNode;

import java.util.*;

public class GraphMapper {
    /**
     * map GraphNode graph into InputNode graph
     *
     * @param graphNode
     * @param depth
     * @param pos
     * @return
     */
    private static InputNode graphNodesToInputNodes(GraphNode graphNode, int depth, int pos) {
        InputNode inputNode = InputNode.create(
                graphNode.__id(),
                graphNode.__projectId(),
                graphNode.__source(),
                graphNode.getJson(), graphNode.getType(), depth, pos);
        inputNode.setDead(graphNode.isDead());
        inputNode.setReason(graphNode.getReason());
        inputNode.setType(graphNode.getType());
        ArrayList<InputNode> linked = new ArrayList<>();
        if (graphNode.getLinked() != null) {
            for (int i = 0; i < graphNode.getLinked().size(); i++) {
                linked.add(graphNodesToInputNodes(graphNode.getLinked().get(i), depth + 1, i));
            }
        }
        inputNode.linkAll(linked);
        return inputNode;
    }

    public static InputNode graphNodesToInputNodes(GraphNode graphNode) {
        return graphNodesToInputNodes(graphNode, 0, 0);
    }

    public static class GraphNodeMapper {
        private GraphNode root;

        public GraphNodeMapper(GraphNode root) {
            this.root = root;
        }

        public InputNode asInputNode() {
            InputNode root = createNode(this.root, 0, 0);
            InputNode lastNode = root;
            do {
                if (!lastNode.hasChildren()) {
                    scan(lastNode);
                }
                lastNode = selectNext(lastNode);
            } while (lastNode != null);

            return root;
        }

        private InputNode selectNext(InputNode last) {
            InputNode selected = next(last);
            if (selected == null) {
                for (InputNode neighbor : last.neighborhood()) {
                    if (neighbor.hasChildren() && !neighbor.isDead()) {
                        return neighbor.child(0);
                    }
                }
                return null;
            }
            return selected;
        }


        public InputNode next(InputNode node) {
            List<InputNode> neighbors = new ArrayList<>(node.neighborhood());
            for (int i = 0; i < neighbors.size(); i++) {
                if (neighbors.get(i).equals(node)) {
                    if (i + 1 < neighbors.size()) {
                        return neighbors.get(i + 1);
                    }
                }
            }
            return null;
        }

        private void scan(InputNode lastNode) {
            if (lastNode.location().equals(InputLocation.START)) {
                int pos = 0;
                for (GraphNode graphNode : root.getLinked()) {
                    lastNode.link(createNode(graphNode, 1, pos++));
                }
            } else {
                GraphNode node = navigate(root, lastNode.getId(), new ArrayList<>());
                if (node != null) {
                    int pos = 0;
                    for (GraphNode graphNode : node.getLinked()) {
                        lastNode.link(createNode(graphNode, lastNode.location().getDepth(), pos++));
                    }
                }
            }
        }

        private GraphNode navigate(GraphNode graphNode, long id, ArrayList<Long> scanned) {
            if (graphNode.__id() == id) return graphNode;
            for (GraphNode node : graphNode.getLinked()) {
                if (!scanned.contains(node.__id())) {
                    scanned.add(node.__id());
                    return navigate(node, id, scanned);
                }
            }
            return null;
        }

        private InputNode createNode(GraphNode graphNode, int depth, int pos) {
            InputNode node = InputNode.create(
                    graphNode.__id(),
                    graphNode.__projectId(),
                    graphNode.__source(),
                    graphNode.getJson(), graphNode.getType(), depth, pos);
            node.setDead(graphNode.isDead());
            node.setReason(graphNode.getReason());
            return node;
        }
    }

    /**
     * map InputNode graph into GraphNode graph
     *
     * @param inputNode
     * @return
     */
    public static GraphNode inputNodesToGraphNodes(InputNode inputNode) {
        GraphNode node = new GraphNode(inputNode.getCompositeId());
        node.setDead(inputNode.isDead());
        node.setReason(inputNode.getReason());
        node.setJson(inputNode.getJson());
        node.setType(inputNode.getType());
        ArrayList<GraphNode> linked = new ArrayList<>();
        for (int i = 0; i < inputNode.children().size(); i++) {
            linked.add(inputNodesToGraphNodes(inputNode.child(i)));
        }
        node.setLinked(linked);
        return node;
    }


}
