package com.arafeh.jsf.dal;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.config.AppProperties;
import com.arafeh.jsf.core.utils.Extensions;
import com.arafeh.jsf.core.utils.StaticResources;
import com.arafeh.jsf.model.GraphNode;
import dynamicore.input.node.NodeJsonConverter;
import dynamicore.input.node.InputNode;
import org.junit.Test;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.StatementResult;

import java.util.Optional;

import static com.arafeh.jsf.core.utils.Extensions.floatVal;
import static com.arafeh.jsf.core.utils.Extensions.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.neo4j.driver.v1.Values.parameters;

public class GraphTest {
    @Test
    public void test1() {
        InputNode node = StaticResources.graph(6);
        InputNode parent = node.child(1).child(1);
        InputNode child1 = parent.child(1);
        InputNode child11 = parent.child(1).child(1);
        InputNode child2 = parent.child(1);
        InputNode child22 = parent.child(2).child(2);

        assertEquals(child1.parent(), parent);
        assertTrue(child1.neighborhood().contains(child2));
        assertTrue(child11.neighborhood().contains(child22));
    }

    @Test
    public void test2() {
        InputNode node = StaticResources.graph(2);
        System.out.println(NodeJsonConverter.convert(node));
    }


    @Test
    public void graphRetrialTest2() {
        GraphNodeBll graphNodeBll = new GraphNodeBll();
        graphNodeBll.init();
        GraphNode graphNode = new GraphNode("1633055195818005612:4_RandomInputSource");
        graphNode.setJson("{}");
        graphNode.setDead(false);
        graphNode.setReason("holla");
        graphNodeBll.save(graphNode);
        Optional<GraphNode> graph = graphNodeBll.get("1633055195818005612:4_RandomInputSource");
        System.out.println(graph);
        graphNodeBll.delete(graph.get());
    }

    @Test
    public void graphRetrialTest3() {
        GraphNodeBll graphNodeBll = new GraphNodeBll();
        graphNodeBll.init();
        Optional<GraphNode> graph = graphNodeBll.get("1633057695270644636:3_TwitterInputSource");
        System.out.println(graph);
    }

    @Test
    public void tests() {
        System.out.println(Long.valueOf("0.5").longValue());
        System.out.println(Extensions.floatVal("0.5"));
    }
}
