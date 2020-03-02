package com.arafeh.jsf.dal;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.RelationNodeBll;
import com.arafeh.jsf.core.utils.RandomExtension;
import com.arafeh.jsf.core.utils.TextGenerator;
import com.arafeh.jsf.model.GraphNode;
import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.NodeType;
import dynamicore.input.node.InputNode;
import dynamicore.xc_input.sql.SQLInputSource;
import dynamicore.xc_input.sql.SQLSourceDataProvider;
import jdk.internal.util.xml.impl.Input;
import org.junit.Test;

import java.util.*;

import static com.arafeh.jsf.core.utils.Extensions.in;
import static com.arafeh.jsf.core.utils.Extensions.random;
import static java.lang.Math.round;

public class RelationNodeTest {
    @Test
    public void create() {
        RelationNodeBll relationNodeBll = new RelationNodeBll();
        relationNodeBll.init();
        relationNodeBll.clear();

        GraphNodeBll graphNodeBll = new GraphNodeBll();
        graphNodeBll.init();
        graphNodeBll.clear();

        cycle(relationNodeBll);

        GraphNode graphNode = relationNodeBll.buildNeo4jGraph(1);
        graphNodeBll.save(graphNode);


    }

    private InputNode node() {
        int id = random().nextInt(Integer.MAX_VALUE);
        System.out.println(id);
        InputNode inputNode = InputNode.create(id, 10, "Twitter", "{}", NodeType.FOLLOWER, 0, 0);
        inputNode.setType(NodeType.FOLLOWER);
        return inputNode;
    }

    @Test
    public void testSave() {
        RelationNodeBll relationNodeBll = new RelationNodeBll();
        relationNodeBll.init();
        relationNodeBll.clear();
        InputNode parent = node();
        InputNode child1 = node();
        InputNode child2 = node();
        InputNode child3 = node();
        InputNode child4 = node();
        InputNode child5 = node();
        InputNode child6 = node();
        InputNode child7 = node();
        InputNode child8 = node();
        parent.link(child1);
        parent.link(child2);
        parent.link(child3);
        parent.link(child4);
        parent.link(child5);
        parent.link(child6);
        parent.link(child7);
        parent.link(child8);
        relationNodeBll.saveNode(parent);
        child8.setDead(true);
        child1.setDead(true);
        child2.setDead(true);
        relationNodeBll.saveNode(parent);
    }

    @Test
    public void testSaveNeo() {
        RelationNodeBll relationNodeBll = new RelationNodeBll();
        relationNodeBll.init();
        InputNode parent = node();
        InputNode child1 = node();
        InputNode child2 = node();
        InputNode child3 = node();
        InputNode child4 = node();
        InputNode child5 = node();
        InputNode child6 = node();
        InputNode child7 = node();
        InputNode child8 = node();
        parent.link(child1);
        parent.link(child2);
        parent.link(child3);
        parent.link(child4);
        parent.link(child5);
        parent.link(child6);
        parent.link(child7);
        parent.link(child8);
        relationNodeBll.saveNodeNeo4j(parent);
        child1.setDead(true);
        child2.setDead(true);
        relationNodeBll.saveNodeNeo4j(parent);
    }

    @Test
    public void testSave2() {
        RelationNodeBll relationNodeBll = new RelationNodeBll();
        relationNodeBll.init();
        relationNodeBll.clear();
        InputNode parent = node();
        for (int i = 0; i < 5000; i++) {
            parent.link(node());
        }
        long time = System.nanoTime();
        relationNodeBll.saveNode(parent);
        time = System.nanoTime() - time;
        System.out.println("save " + time / 1000);
        for (int i = 0; i < 2000; i++) {
            parent.children().get(i).setDead(true);
        }
        time = System.nanoTime();
        relationNodeBll.saveNode(parent);
        time = System.nanoTime() - time;
        System.out.println("update " + time / 1000);
        System.out.println(relationNodeBll.count());
    }

    private InputNode simple() {
        InputNode parent = node();
        for (int i = 0; i < 20; i++) {
            parent.link(node());
        }
        return parent;
    }

    private void cycle(RelationNodeBll relationNodeBll) {
        InputNode root = node();

        ArrayList<InputNode> children = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            children.add(node());
        }

        for (InputNode child : children) {
            root.link(child);
        }

        relationNodeBll.saveNode(root);

        children.get(0).link(children.get(1));
        children.get(0).link(children.get(2));
        children.get(0).link(children.get(3));
        relationNodeBll.saveNode(root.children().get(0));

        children.get(1).link(children.get(0));
        children.get(1).link(children.get(2));
        children.get(1).link(children.get(3));
        relationNodeBll.saveNode(root.children().get(1));

        children.get(2).link(children.get(0));
        children.get(2).link(children.get(1));
        relationNodeBll.saveNode(root.children().get(2));

        children.get(3).link(children.get(1));
        children.get(3).link(children.get(0));
        children.get(3).link(children.get(2));
        relationNodeBll.saveNode(root.children().get(3));

        children.get(4).link(children.get(3));
        children.get(4).link(children.get(2));
        children.get(4).link(children.get(0));
        relationNodeBll.saveNode(root.children().get(4));

    }
}
