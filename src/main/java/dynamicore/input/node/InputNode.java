package dynamicore.input.node;


import com.arafeh.jsf.core.protocols.Action;
import com.arafeh.jsf.core.protocols.Function;
import com.arafeh.jsf.core.utils.Var;
import com.arafeh.jsf.model.NodeType;

import java.util.*;


public class InputNode extends BaseNode<InputNode> {

    public static InputNode create(long id, long projectId, String source, String json, NodeType type, int depth, int pos) {
        InputNode inputNode = new InputNode();
        inputNode.setId(id);
        inputNode.setProjectId(projectId);
        inputNode.setDead(false);
        inputNode.setSource(source);
        inputNode.setJson(json);
        inputNode.setType(type);
        inputNode.setInputLocation(new InputLocation(depth, pos));
        return inputNode;
    }

    protected List<InputNode> children;
    protected InputNode parent;

    private InputNode() {
        children = emptyList();
    }

    private List<InputNode> emptyList() {
        return new ArrayList<>();
    }

    //TODO: check for cycles
    @Override
    public void link(InputNode node) {
        node.parent = this;
        this.children.add(node);
    }

    private boolean isAncestor(InputNode node) {
        InputNode parent = parent();
        while (parent != null) {
            if (parent.getId() == node.getId()) return true;
            parent = parent.parent();
        }
        return false;
    }

    @Override
    public void linkAll(Collection<InputNode> nodes) {
        for (InputNode node : nodes) {
            this.link(node);
        }
    }

    @Override
    public List<InputNode> parents() {
        List<InputNode> set = emptyList();
        set.add(parent);
        return set;
    }

    @Override
    public InputNode parent() {
        return parent;
    }

    @Override
    public List<InputNode> children() {
        return this.children;
    }

    @Override
    public InputNode child(int pos) {
        return new ArrayList<>(children()).get(pos);
    }

    @Override
    public List<InputNode> siblings() {
        List<InputNode> nodes = emptyList();
        if (parent != null) {
            nodes = parent.children();
        }
        return nodes;
    }

    @Override
    public List<InputNode> neighborhood() {
        List<InputNode> nodes;
        if (parent == null) {
            nodes = emptyList();
            nodes.add(this);
        } else if (parent.parent == null) {
            nodes = siblings();
        } else {
            nodes = emptyList();
            InputNode grand = this.parent.parent;
            for (InputNode parent : grand.children()) {
                nodes.addAll(parent.children());
            }
        }

        return nodes;
    }

    @Override
    public InputNode root() {
        InputNode current = this;
        while (current.parent != null) {
            current = current.parent;
        }
        return current;
    }

    @Override
    public InputNode navigate(long id) {
        if (this.getId() == id) return this;
        if (this.children() == null) return null;
        for (InputNode link : this.children()) {
            InputNode nav = link.navigate(id);
            if (nav != null) return nav;
        }
        return null;
    }


    @Override
    public boolean hasChildren() {
        return children().size() > 0;
    }


    @Override
    public boolean exists(InputNode node) {
        final Var<Boolean> exists = new Var<>(false);
        root().each(n -> {
            if (n.equals(node)) exists.set(true);
        });
        return exists.get();
    }

    @Override
    public void each(Action<InputNode> action) {
        each(action, new ArrayList<>());
    }

    @Override
    public int size() {
        return size(null);
    }

    @Override
    public int size(Function<Boolean, InputNode> where) {
        Var<Integer> size = new Var<>(0);
        each(inputNode -> {
            if (where == null || where.run(inputNode)) {
                size.set(size.get() + 1);
            }
        });
        return size.get();
    }

    private void each(Action<InputNode> action, ArrayList<InputNode> executed) {
        action.run(this);
        if (executed.contains(this)) return;
        executed.add(this);
        if (this.children() == null) return;
        for (InputNode node : this.children()) {
            node.each(action, executed);
        }
    }

    @Override
    public String compositeFormula() {
        return "{id}:{projectId}:{source}:{parentId}";
    }

    @Override
    protected String getCompositeAttrValue(String property) {
        switch (property) {
            case "parentId":
                if (parent != null) return String.valueOf(this.parent.getId());
        }
        return super.getCompositeAttrValue(property);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        InputNode node = ((InputNode) obj);
        if (this.parent() != null && node.parent() != null) {
            equals = equals && node.parent().getId() == this.parent().getId();
        }
        return equals;
    }
}
