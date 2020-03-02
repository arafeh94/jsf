package com.arafeh.jsf.dal;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.bll.RelationNodeBll;
import com.arafeh.jsf.model.GraphNode;
import com.arafeh.jsf.service.Neo4jExecutorService;
import dynamicore.input.Breadcrumb;
import org.junit.Test;

import java.util.logging.Level;

public abstract class TestDynamicNetwork {
    public static int PJ_ID = 3;
    GraphNodeBll graphNodeBll;
    ProjectBll projectBll;
    NodeBll nodeBll;
    RelationNodeBll relationNodeBll;
    Neo4jExecutorService neo4jExecutorService;

    protected void init() {
        graphNodeBll = new GraphNodeBll();
        graphNodeBll.init();
        nodeBll = new NodeBll();
        nodeBll.init();
        projectBll = new ProjectBll();
        projectBll.init();
        relationNodeBll = new RelationNodeBll();
        relationNodeBll.init();
        neo4jExecutorService = new Neo4jExecutorService();
        neo4jExecutorService.init();
    }

    protected void clear() {
        graphNodeBll.clear();
        projectBll.clear();
        relationNodeBll.clear();
        Breadcrumb.purge();
    }


    public abstract void test() throws InstantiationException, IllegalAccessException, ClassNotFoundException;


}
