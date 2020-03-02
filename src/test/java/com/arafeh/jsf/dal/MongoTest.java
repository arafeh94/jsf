package com.arafeh.jsf.dal;

import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.core.utils.TextGenerator;
import com.arafeh.jsf.dal.datasource.MongoDataSource;
import com.arafeh.jsf.model.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import dynamicore.Static_Settings;
import dynamicore.xc_analysis.NoneAnalysis;
import dynamicore.xc_analysis.PageRankAnalysis;
import dynamicore.xc_input.random.RandomInputSource;
import dynamicore.xc_navigator.BFSNavigator;
import org.bson.Document;
import org.junit.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

public class MongoTest {
    @Test
    public void test1() {
        MongoDataSource dataSource = new MongoDataSource();
        dataSource.connect();
        MongoDatabase database = dataSource.client().getDatabase("socins");

        for (Document document : database.getCollection("relationNodes").find(eq("type", "ACCOUNT"))) {
            long accountId = (long) document.get("left");
            System.out.println(accountId);
            System.out.println(database.getCollection("relationNodes").count(eq("right", accountId)));
//            FindIterable<Document> query = database.getCollection("relationNodes").find(eq("right", account));
//            for (Document follower : query) {
//                long followerId = (long) follower.get("left");
//                Document followerInfo = database.getCollection("nodes").find(eq("_id", followerId)).first();
//                System.out.println(followerInfo);
//
//            }

        }
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void test12() {
        ProjectBll projectBll = new ProjectBll();
        projectBll.init();
        projectBll.clear();

        Project project = new Project(4, "random4");
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(PageRankAnalysis.class.getName());
            add(NoneAnalysis.class.getName());
        }});
        project.setDataSource(new ProjectDataSource() {{
            setSource(RandomInputSource.class.getName());
            setStartingIds("1;2;3;4");
            addSetting(Static_Settings.RandomInputSourceSettings.MAX_DEPTH, "5", "");
            addSetting(Static_Settings.RandomInputSourceSettings.SEED, "987", "");
            addSetting(Static_Settings.Filters.MAX_LEVEL, "3", "");
            addSetting(Static_Settings.Filters.MIN_FOLLOWERS_THRESHOLD, "2", "");
            addSetting(Static_Settings.Filters.MAX_FETCHES, "3", "");
        }});
        project.setNavigatorStrategy(BFSNavigator.class.getName());

        projectBll.set(project);

        ProjectDataSource ds = project.getDataSource();
        ds.setGraphId("123");
        projectBll.set(project);
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void test123() {
        ProjectBll projectBll = new ProjectBll();
        projectBll.init();
        Project project = projectBll.get(4).get();
        project.setState(ProjectState.FINISHED);
        projectBll.set(project);
    }

    @Test
    public void test11() {
        NodeBll nodeBll = new NodeBll();
        nodeBll.init();
        System.out.println(nodeBll.all().stream().filter(n -> n.getType().equals("POST")).collect(Collectors.toList()));
        Node node = createNode(1);
        node.setType("SAMIRA");
        node.set("isVerified", "True");
        node.set("isProtected", "True");
        nodeBll.set(node);
    }

    @Test
    public void test21() {
        NodeBll nodeBll = new NodeBll();
        nodeBll.init();
        nodeBll.all().stream().filter(n ->
                n.getType().equals("POST")).forEach(n -> {
            System.out.println(n.toString());
        });
    }

    public Node createNode(int id) {
        Node node = new Node();
        node.setId(id);
        node.store("Name", TextGenerator.getInstance().getFullName());
        node.store("AccountName", TextGenerator.getInstance().getScreenName());
        node.store("Email", TextGenerator.getInstance().getEmail());
        return node;
    }
}
