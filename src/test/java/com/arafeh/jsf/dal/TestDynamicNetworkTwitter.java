package com.arafeh.jsf.dal;

import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.core.utils.Func;
import com.arafeh.jsf.model.GraphNode;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.DynamicNetwork;
import dynamicore.Static_Settings;
import dynamicore.xc_analysis.PageRankAnalysis;
import dynamicore.xc_input.twitter.TwitterInputSource;
import dynamicore.xc_navigator.BFSNavigator;
import dynamicore.xc_navigator.RandomWalkNavigator;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TestDynamicNetworkTwitter extends TestDynamicNetwork {
    @Test
    public void test() {
        init();
        clear();
        Project project = new Project(3, "twitter");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(PageRankAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(TwitterInputSource.class.getName());
            setStartingIds("@virginiaraggi;@BeppeSala;@c_appendino;@LeolucaOrlando1");
            addSetting(Static_Settings.Filters.MAX_LEVEL, "3", null);
            addSetting(Static_Settings.Filters.MAX_FETCHES, "5", null);
            addSetting(Static_Settings.Filters.MIN_FOLLOWERS_THRESHOLD, "2", null);
            addSetting(Static_Settings.InputSourceSettings.MAX_LOADED_NODES, "10", null);
//            addSetting(Static_Settings.InputSourceSettings.INCLUDE_TWEETS, "5", null);
        }});
        project.setNavigatorStrategy(BFSNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
    }


    @Test
    public void test21() throws IOException, InterruptedException {
        init();
        clear();
        Project project = new Project(1, "twitter_bpm");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(PageRankAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(TwitterInputSource.class.getName());
            setStartingIds("@BPMConf");
            addSetting(Static_Settings.Filters.MAX_LEVEL, "2", null);
            addSetting(Static_Settings.Filters.MAX_FETCHES, "5", null);
            addSetting(Static_Settings.Filters.ONTO_MAPPER, "", null);
            addSetting(Static_Settings.InputSourceSettings.MAX_LOADED_NODES, "5", null);
        }});
        project.setNavigatorStrategy(BFSNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
    }

    @Test
    public void test12() {
        init();
        clear();
        Project project = new Project(1, "twitter");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(PageRankAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(TwitterInputSource.class.getName());
            setStartingIds("@lumorisi;@AnnalisaChirico;@giodiamanti;@claudiovelardi;@gcomin;@fnicodemo;@pietroraffa;@marodri;@SporcoLobbista;@laelius;@lorepregliasco;@Martina_Carone;@smenichini;@Marco_Cacciotto;@gditom");
            addSetting(Static_Settings.Filters.MAX_LEVEL, "3", null);
            addSetting(Static_Settings.Filters.MAX_FETCHES, "200", null);
            addSetting(Static_Settings.InputSourceSettings.MAX_LOADED_NODES, "5000", null);
        }});
        project.setNavigatorStrategy(RandomWalkNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
    }

    @Test
    public void test13() {
        init();
        clear();
        Project project = new Project(1, "twitter_random_ali");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(PageRankAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(TwitterInputSource.class.getName());
            setStartingIds("@Q_Army1;@chrisoldcorn;@Chuck_Freedom;@music08444583;@Conniebach;@AfroLeadership_;@RealHKNews;@ErinSnider10;@Raven_kun;@The_Queen_G;@AuburnPulse;@Minajabbarian;@eaparkstweet;@PPCLangley_ALDG;@LinHamilton11;@FaridRazaqi;@qualityeo;@globalvoices;@philmcmahon2;@kostisroussos;");
            addSetting(Static_Settings.Filters.MAX_LEVEL, "4", null);
            addSetting(Static_Settings.Filters.MAX_FETCHES, "10", null);
            addSetting(Static_Settings.RandomWalkSettings.ITERATIONS, "50", null);
            addSetting(Static_Settings.Filters.MIN_FOLLOWERS_THRESHOLD, "50", null);
            addSetting(Static_Settings.InputSourceSettings.MAX_LOADED_NODES, "500", null);
        }});
        project.setNavigatorStrategy(RandomWalkNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
    }

    @Test
    public void test14() {
        init();
//        clear();
        Project project = new Project(3, "twitter_normal2_ali");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(PageRankAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(TwitterInputSource.class.getName());
            setStartingIds("@enki74;@Q_Army1;@IAM__Network;@StevennBeck1;@pegchandler;@Teepee1954;@chrisoldcorn;@AvalancheManGod;@wstj4330;@fahr451jpn;@Doudou_dur;@indepdnce_ch;@freedombhoot;@freeddeplorable;@fromjpn1111;@JtheKruse;@aytgh;@jstc4nmlstx;@FatalPolitics;@spamzen");
            addSetting(Static_Settings.Filters.MAX_LEVEL, "4", null);
            addSetting(Static_Settings.Filters.MAX_FETCHES, "10", null);
            addSetting(Static_Settings.RandomWalkSettings.ITERATIONS, "50", null);
            addSetting(Static_Settings.Filters.MIN_FOLLOWERS_THRESHOLD, "50", null);
            addSetting(Static_Settings.InputSourceSettings.MAX_LOADED_NODES, "500", null);
        }});
        project.setNavigatorStrategy(RandomWalkNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
    }

    @Test
    public void buildGraph() {
        init();
        GraphNode root = relationNodeBll.buildNeo4jGraph(1643542840966405992L);
        graphNodeBll.save(root);
    }

    @Test
    public void testWithTweets() {
        init();
        clear();
        Project project = new Project(3, "twitter");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(PageRankAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(TwitterInputSource.class.getName());
            setStartingIds("virginiaraggi;#dog;#cats");
            addSetting(Static_Settings.Filters.MAX_LEVEL, "3", null);
            addSetting(Static_Settings.Filters.MAX_FETCHES, "5", null);
            addSetting(Static_Settings.InputSourceSettings.MAX_LOADED_NODES, "5", null);
        }});
        project.setNavigatorStrategy(BFSNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
    }

    @Test
    public void tweets() {
        init();
        clear();
        Project project = new Project(4, "tweets");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(PageRankAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(TwitterInputSource.class.getName());
            setStartingIds("#sport");
            addSetting(Static_Settings.Filters.MAX_LEVEL, "4", null);
            addSetting(Static_Settings.Filters.MAX_FETCHES, "100", null);
            addSetting(Static_Settings.Filters.MIN_FOLLOWERS_THRESHOLD, "2", null);
            addSetting(Static_Settings.InputSourceSettings.MAX_LOADED_NODES, "5", null);
        }});
        project.setNavigatorStrategy(BFSNavigator.class.getName());
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
    }

    @Test
    public void resume() {
        init();
        Project project = new ProjectBll().get(3).get();
        DynamicNetwork dynamicNetwork = DynamicNetwork.from(project);
        assert dynamicNetwork != null;
        dynamicNetwork.execute();
    }


}
