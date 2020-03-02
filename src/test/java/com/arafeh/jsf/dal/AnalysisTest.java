package com.arafeh.jsf.dal;


import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.core.utils.HashMap;
import com.arafeh.jsf.model.GraphNode;
import com.arafeh.jsf.model.Project;
import dynamicore.analysing.DataAnalysisBase;
import dynamicore.xc_analysis.DegreeCentralityAnalysis;
import dynamicore.xc_analysis.EigenvectorCentralityAnalysis;
import dynamicore.xc_analysis.PageRankAnalysis;
import dynamicore.xc_input.twitter.TwitterInputSource;
import org.junit.Test;

import java.text.MessageFormat;

import static com.arafeh.jsf.bll.GraphNodeBll.*;

public class AnalysisTest extends TestDynamicNetwork {

    public <T extends DataAnalysisBase> T algo(long projectId, Class<? extends T> algoClass) throws IllegalAccessException, InstantiationException {
        init();
        T init = algoClass.newInstance();
        Project project = this.projectBll.get(projectId).get();
        init.init(project, project.getDataSource(), projectBll, nodeBll, graphNodeBll);
        return init;
    }

    @Test
    public void pageRank() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        PageRankAnalysis analysis = algo(2, PageRankAnalysis.class);
        System.out.println(analysis.analyse());
    }

    @Test
    public void degreeCentrality() throws InstantiationException, IllegalAccessException {
        DegreeCentralityAnalysis analysis = algo(1, DegreeCentralityAnalysis.class);
        System.out.println(analysis.analyse());
    }

    @Test
    public void eigenvectorCentrality() throws InstantiationException, IllegalAccessException {
        EigenvectorCentralityAnalysis analysis = algo(1, EigenvectorCentralityAnalysis.class);
        System.out.println(analysis.analyse());
    }

    @Test
    public void buildGraph() {
        init();
        System.out.println("building neo4j graph");
        long time = System.nanoTime();
        GraphNode root = relationNodeBll.buildNeo4jGraph(1);
        time = System.nanoTime() - time;
        System.out.print("took: ");
        System.out.println(time / 1000 / 60);
        System.out.println("saving neo4j graph");
        graphNodeBll.save(root);
        System.out.print("took: ");
        time = System.nanoTime() - time;
        System.out.println(time / 1000 / 60);
    }

    @Override
    @Test
    public void test() {

    }

}
