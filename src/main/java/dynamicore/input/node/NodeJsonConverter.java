package dynamicore.input.node;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;

public class NodeJsonConverter {
    private static String convert(InputNode inputNode, HashSet<Node> nodes, ArrayList<Link> links) {
        nodes.add(new Node(inputNode.getId()));
        for (InputNode child : inputNode.children()) {
            convert(child, nodes, links);
            links.add(new Link(inputNode.getId(), child.getId()));
        }
        return new Gson().toJson(new JsonWrapper(nodes, links));
    }

    public static String convert(InputNode node) {
        return convert(node, new HashSet<>(), new ArrayList<>());
    }

    public static class JsonWrapper {
        public HashSet<Node> nodes;
        public ArrayList<Link> links;

        public JsonWrapper() {

        }

        public JsonWrapper(HashSet<Node> nodes, ArrayList<Link> links) {
            this.nodes = nodes;
            this.links = links;
        }
    }

    public static class Node {
        public long id;

        public Node() {

        }

        public Node(long id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Node) {
                return ((Node) obj).id == this.id;
            }
            return false;
        }
    }

    public static class Link {
        public long source;
        public long target;

        public Link() {

        }

        public Link(long source, long target) {
            this.source = source;
            this.target = target;
        }
    }
}
