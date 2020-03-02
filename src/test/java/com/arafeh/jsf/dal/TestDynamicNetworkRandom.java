package com.arafeh.jsf.dal;

import com.arafeh.jsf.core.protocols.Function;
import com.arafeh.jsf.core.utils.Func;
import com.arafeh.jsf.model.GraphNode;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.DynamicNetwork;
import dynamicore.Static_Settings;
import dynamicore.analysing.Table;
import dynamicore.input.Breadcrumb;
import dynamicore.xc_analysis.AggregationAnalysis;
import dynamicore.xc_analysis.NoneAnalysis;
import dynamicore.xc_analysis.PageRankAnalysis;
import dynamicore.xc_input.random.RandomInputSource;
import dynamicore.xc_input.sql.SQLInputSource;
import dynamicore.xc_input.twitter.TwitterInputSource;
import dynamicore.xc_navigator.BFSNavigator;
import dynamicore.xc_navigator.MetropolisHastingNavigator;
import dynamicore.xc_navigator.RandomWalkNavigator;
import org.junit.Test;

import java.util.ArrayList;

import static com.arafeh.jsf.core.utils.Extensions.except;

public class TestDynamicNetworkRandom extends TestDynamicNetwork {
    public static int PJ_ID = 4;

    @Test
    public void analyse() {
        init();
        Project project = projectBll.all().get(0);
        ArrayList<Table> tables = DynamicNetwork.from(project).analyse();
        for (Table table : tables) {
            System.out.println(table);
        }
    }

    @Test
    @Override
    public void test() {
        init();
        clear();
        Project project = new Project(4, "random4");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(PageRankAnalysis.class.getName());
            add(NoneAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(RandomInputSource.class.getName());
            setStartingIds("1;2;3;4");
            addSetting(Static_Settings.RandomInputSourceSettings.MAX_DEPTH, "3", "");
            addSetting(Static_Settings.RandomInputSourceSettings.SEED, "987", "");
        }});
        project.setNavigatorStrategy(BFSNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
    }

    @Test
    public void testRandomWalker() {
        init();
        clear();
        Project project = new Project(5, "random_walk");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(PageRankAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(RandomInputSource.class.getName());
            setStartingIds("1;2;3;4");
            addSetting(Static_Settings.RandomInputSourceSettings.MAX_DEPTH, "8", "");
            addSetting(Static_Settings.RandomInputSourceSettings.SEED, "988", "");
            addSetting(Static_Settings.RandomWalkSettings.ITERATIONS, "100", "");
            addSetting(Static_Settings.RandomWalkSettings.FORWARD_WEIGHT, "0.2", "");
        }});
        project.setNavigatorStrategy(RandomWalkNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
        Breadcrumb breadcrumb = Breadcrumb.load(5);
        System.out.println(breadcrumb);
    }
    @Test
    public void testRandomWalkerBPM() {
        init();
        clear();
        Project project = new Project(6, "random_walk_bpm");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(PageRankAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(TwitterInputSource.class.getName());
            setStartingIds("1;2;3;4");
            addSetting(Static_Settings.RandomInputSourceSettings.MAX_DEPTH, "8", "");
            addSetting(Static_Settings.RandomInputSourceSettings.SEED, "988", "");
            addSetting(Static_Settings.RandomWalkSettings.ITERATIONS, "100", "");
            addSetting(Static_Settings.RandomWalkSettings.FORWARD_WEIGHT, "0.2", "");
        }});
        project.setNavigatorStrategy(RandomWalkNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
        Breadcrumb breadcrumb = Breadcrumb.load(5);
        System.out.println(breadcrumb);
    }


    @Test
    public void testBFSSQLFULL() {
        init();
        clear();
        long executionTime = System.nanoTime();
        Project project = new Project(6, "bfs_sql");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(AggregationAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(SQLInputSource.class.getName());
            setStartingIds("1963516285;1496541763;2405908961;3245100357");
            addSetting(Static_Settings.Filters.MAX_LEVEL, "8", "");
            addSetting(Static_Settings.Filters.MAX_FETCHES, "0", "");
            addSetting(Static_Settings.Filters.MIN_FRENDS, "0", "");
            addSetting(Static_Settings.InputSourceSettings.MAX_LOADED_NODES, "1000", "");
        }});
        project.setNavigatorStrategy(BFSNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
        for (Table table : dynamicNetwork.analyse()) {
            System.out.println(table);
        }
        System.out.println(System.nanoTime() - executionTime);
        System.out.println("SCANS: "+SQLInputSource.SCAN_REQ_COUNT);
        System.out.println("FETCH: "+SQLInputSource.FETCH_REQ_COUNT);
    }

    @Test
    public void testBFSSQL() {
        init();
        clear();
        Project project = new Project(6, "bfs_sql");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(AggregationAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(SQLInputSource.class.getName());
            setStartingIds("1963516285;1496541763;2405908961;3245100357");
            addSetting(Static_Settings.Filters.MAX_LEVEL, "3", "");
            addSetting(Static_Settings.Filters.MAX_FETCHES, "20", "");
            addSetting(Static_Settings.Filters.MIN_FRENDS, "200", "");
            addSetting(Static_Settings.InputSourceSettings.MAX_LOADED_NODES, "100", "");
        }});
        project.setNavigatorStrategy(BFSNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
        for (Table table : dynamicNetwork.analyse()) {
            System.out.println(table);
        }

        System.out.println("SCANS: "+SQLInputSource.SCAN_REQ_COUNT);
        System.out.println("FETCH: "+SQLInputSource.FETCH_REQ_COUNT);
    }

    @Test
    public void testRWSQL() {
        init();
        clear();
        Project project = new Project(7, "rw_sql");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(AggregationAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(SQLInputSource.class.getName());
            setStartingIds("1963516285;1496541763;2405908961;3245100357");
            addSetting(Static_Settings.Filters.MAX_FETCHES, "30", "");
            addSetting(Static_Settings.Filters.MIN_FRENDS, "200", "");
            addSetting(Static_Settings.InputSourceSettings.MAX_LOADED_NODES, "100", "");
            addSetting(Static_Settings.RandomWalkSettings.FORWARD_WEIGHT, "0.8", "");
            addSetting(Static_Settings.RandomWalkSettings.ITERATIONS, "80", "");
        }});
        project.setNavigatorStrategy(RandomWalkNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
        for (Table table : dynamicNetwork.analyse()) {
            System.out.println(table);
        }
        System.out.println("SCANS: "+SQLInputSource.SCAN_REQ_COUNT);
        System.out.println("FETCH: "+SQLInputSource.FETCH_REQ_COUNT);
    }

    @Test
    public void testMH() {
        init();
        clear();
        Project project = new Project(7, "rw_sql");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(AggregationAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(SQLInputSource.class.getName());
            setStartingIds("1963516285;1496541763;2405908961;3245100357");
            addSetting(Static_Settings.Filters.MAX_FETCHES, "50", "");
            addSetting(Static_Settings.InputSourceSettings.MAX_LOADED_NODES, "100", "");
            addSetting(Static_Settings.RandomWalkSettings.FORWARD_WEIGHT, "0.2", "");
            addSetting(Static_Settings.RandomWalkSettings.ITERATIONS, "80", "");
            addSetting(Static_Settings.MetropolisHasting.DISTRIBUTION, "gaussian", "");
            addSetting(Static_Settings.MetropolisHasting.MEAN, "0", "");
            addSetting(Static_Settings.MetropolisHasting.STANDARD_DEVIATION, "1", "");
        }});
        project.setNavigatorStrategy(MetropolisHastingNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
        for (Table table : dynamicNetwork.analyse()) {
            System.out.println(table);
        }
        System.out.println("SCANS: "+SQLInputSource.SCAN_REQ_COUNT);
        System.out.println("FETCH: "+SQLInputSource.FETCH_REQ_COUNT);
    }


}
