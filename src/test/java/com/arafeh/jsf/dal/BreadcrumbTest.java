package com.arafeh.jsf.dal;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.core.utils.Pair;
import com.arafeh.jsf.core.utils.StaticResources;
import com.arafeh.jsf.model.GraphNode;
import com.arafeh.jsf.model.NodeType;
import dynamicore.input.Breadcrumb;
import dynamicore.input.node.GraphMapper;
import dynamicore.input.node.InputNode;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class BreadcrumbTest {
    @Test
    public void test1() {
        String query = "MATCH (root:GraphNode {id: '" + "1634679873229591540:6_SQLInputSource" + "'})\n" +
                "CALL algo.pageRank.stream(\"GraphNode\", \"linked\",{iterations:20,sourceNode:root})\n" +
                "YIELD nodeId, score\n" +
                "MATCH (node) WHERE id(node) = nodeId\n" +
                "RETURN node.id AS id,score\n" +
                "ORDER BY score DESC";

        String query2 = "CALL algo.pageRank.stream(\n" +
                " \"MATCH (n:GraphNode{id:'" + "1634679873229591540:6_SQLInputSource" + "'})-[*]-(b) RETURN id(b) as id\",\n" +
                " \"MATCH (p1:GraphNode)<-[:linked]-(p2) where p2.isDead = false and size(p2.json)>0 RETURN id(p2) AS source, id(p1) AS target, count(*) as weight\",\n" +
                " {graph:\"cypher\", weightProperty: \"weight\"})\n" +
                "YIELD nodeId, score\n" +
                "RETURN algo.getNodeById(nodeId).id AS player, score\n" +
                "ORDER BY score DESC";
        System.out.println(query2);
    }

    @Test
    public void test2() {
        GraphNodeBll graphNodeBll = new GraphNodeBll();
        graphNodeBll.init();
        GraphNode root = graphNodeBll.get("1634682395183771272:6_SQLInputSource").get();
        GraphMapper.GraphNodeMapper mapper = new GraphMapper.GraphNodeMapper(root);
        InputNode inputNode = mapper.asInputNode();
        inputNode.each(n -> {
            if (n.hasChildren()) System.out.println(n);

        });

    }

    @Test
    public void test3() {
        InputNode inputNode = InputNode.create(1, 1, "java", "{}", NodeType.FOLLOWER, 0, 0);
        System.out.println(inputNode.getCompositeId());
    }


}
