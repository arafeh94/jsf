package com.arafeh.jsf.dal;

import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.core.protocols.Action;
import com.arafeh.jsf.core.utils.RandomExtension;
import com.arafeh.jsf.core.utils.StaticResources;
import com.arafeh.jsf.core.utils.TextGenerator;
import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.NodeType;
import dynamicore.input.Breadcrumb;
import dynamicore.xc_input.sql.SQLInputSource;
import dynamicore.xc_input.sql.SQLSourceDataProvider;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.arafeh.jsf.core.utils.Extensions.except;
import static com.arafeh.jsf.core.utils.Extensions.random;
import static java.lang.Math.round;
import static org.junit.Assert.assertEquals;

public class SQLTest {
    String[][] langs = {
            {"it", "fr", "it", "it", "it", "en", "it", "it", "ar", "it", "it", "it", "de", "it"},
            {"it", "fr", "fr", "en", "it", "fr", "en", "ar", "de"},
            {"it", "it", "fr", "de", "ar", "en"},
            {"it", "en", "de", "fr", "ar"}
    };
    String[] loc = {"Italy", "Italy", "Italy", "France", "Italy", "UK", "Italy", "Italy", "UAE", "Italy", "Italy"};

    public static String string = "";

    public void print(String msg) {
        string += msg + "\n";
    }

    public void out() {
        System.out.println(string);
    }

    @Test
    public void create() {
        SQLSourceDataProvider sqlSourceDataProvider = SQLSourceDataProvider.getInstance();
        TextGenerator textGenerator = TextGenerator.getInstance(88);
        RandomExtension random = random(88);
        NodeBll nodeBll = new NodeBll();
        nodeBll.init();
        for (int i : new int[]{1, 2, 3, 4}) {
            Node parent = new Node(NodeType.FOLLOWER, "sql");
            parent.setId(i);
            parent.setCreatedAt(new Date());
            parent.store("Email", textGenerator.getEmail());
            parent.store("AccountName", textGenerator.getScreenName());
            parent.store("Name", textGenerator.getFullName());
            parent.store("Description", "none");
            parent.store("Lang", langs[random.nextInt(langs.length)]);
            parent.store("Location", loc[random.nextInt(loc.length)]);
            parent.store("FollowersCount", random.nextInt(10, 10000));
            parent.store("FriendsCount", random.nextInt(10, 10000));
            nodeBll.set(parent);
            ArrayList<Long> ids = sqlSourceDataProvider.getFollowersIDs(i);
            for (Long id : ids) {
                if (!nodeBll.get(id, "sql").isPresent()) {
                    Node follower = new Node(NodeType.FOLLOWER, SQLInputSource.class.getName());
                    follower.setId(id);
                    follower.setCreatedAt(new Date());
                    follower.store("Email", textGenerator.getEmail());
                    follower.store("AccountName", textGenerator.getScreenName());
                    follower.store("Name", textGenerator.getFullName());
                    follower.store("Description", "none");
                    String lang = id < 10 ? "en" : id > 51 && id < 55 ? "de" : langs[i - 1][random.nextInt(langs.length)];
                    follower.store("Lang", lang);
                    follower.store("Location", loc[random.nextInt(loc.length)]);
                    follower.store("FollowersCount", random.nextInt(10, 10000));
                    follower.store("FriendsCount", random.nextInt(10, 10000));
                    nodeBll.set(follower);
                }
            }
        }
    }


    @Test
    public void analyse() {
        SQLSourceDataProvider sqlSourceDataProvider = SQLSourceDataProvider.getInstance();
        NodeBll nodeBll = new NodeBll();
        nodeBll.init();

        for (int i : new int[]{1, 2, 3, 4}) {
            print("analysis for id {" + i + "}");
            print("followers count: " + sqlSourceDataProvider.getFollowersIDs(i).size());
//            sqlSourceDataProvider.query("select t.id, count(*) from (\n" +
//                    "    select `user`.`id`,`user`.`followerId` from `user`\n" +
//                    "    GROUP BY `followerId`\n" +
//                    "    HAVING COUNT(*) = 1\n" +
//                    ") as t where t.id = " + i + "\n" +
//                    "group by t.id", rs -> {
//                try {
//                    print("unique followers:" + rs.getInt(2));
//                } catch (SQLException ignored) {
//                }
//            });
//            print("country distribution");
//            HashMap<String, Integer> countries = new HashMap<>();
//            for (Long id : sqlSourceDataProvider.getFollowersIDs(i)) {
//                String country = nodeBll.get(id, "sql").get().get("Location", "");
//                if (!countries.containsKey(country)) {
//                    countries.put(country, 0);
//                }
//                countries.put(country, countries.get(country) + 1);
//            }
//            countries.forEach((c, cts) -> print(c + ":" + cts));
            print("language distribution");
            HashMap<String, Integer> languages = new HashMap<>();
            ArrayList<Long> followersIDs = sqlSourceDataProvider.getFollowersIDs(i);
            for (Long id : followersIDs) {
                Optional<Node> a = nodeBll.get(id, SQLInputSource.class.getName());
                if (a.isPresent()) {
                    String lang = a.get().get("Lang", "");
                    if (!languages.containsKey(lang)) {
                        languages.put(lang, 0);
                    }
                    languages.put(lang, languages.get(lang) + 1);
                }
            }
            languages.forEach((c, cts) -> {
                double s = round(((double) cts / followersIDs.size()) * 100);
                print(c + ":" + s + "%");
            });
            print("");
        }


        List<Node> all = nodeBll.all();
        HashMap<String, Integer> langMap = new HashMap<>();
        HashMap<String, Integer> locMap = new HashMap<>();
        for (Node node : all) {
            if (!langMap.containsKey(node.get("Lang", ""))) {
                langMap.put(node.get("Lang", ""), 0);
            }
            langMap.put(node.get("Lang", ""), langMap.get(node.get("Lang", "")) + 1);
            if (!locMap.containsKey(node.get("Location", ""))) {
                locMap.put(node.get("Location", ""), 0);
            }
            locMap.put(node.get("Location", ""), locMap.get(node.get("Location", "")) + 1);
        }
        print("Language Distribution");
        langMap.forEach((item, count) -> print(item + ":" + count));
        print("Country Distribution");
        locMap.forEach((item, count) -> print(item + ":" + count));
        out();

    }


    @Test
    public void query() {
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    for (int j = 4; j < 38; j++) {
                        System.out.println("insert into `user` values(" + (i + 1) + "," + (j + 1) + ");");
                    }
                    break;
                case 1:
                    for (int j = 50; j < 120; j++) {
                        System.out.println("insert into `user` values(" + (i + 1) + "," + (j + 1) + ");");
                    }
                    break;
                case 2:
                    for (int j = 120; j < 160; j++) {
                        System.out.println("insert into `user` values(" + (i + 1) + "," + (j + 1) + ");");
                    }
                    break;
                case 3:
                    for (int j = 160; j < 280; j++) {
                        System.out.println("insert into `user` values(" + (i + 1) + "," + (j + 1) + ");");
                    }
                    break;
            }
        }
    }
}
