package dynamicore.xc_input.sql;

import com.arafeh.jsf.config.AppProperties;
import com.arafeh.jsf.core.protocols.Action;
import com.arafeh.jsf.core.utils.Var;
import com.arafeh.jsf.model.Node;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.primefaces.json.JSONArray;
import org.slf4j.LoggerFactory;
import twitter4j.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import static com.arafeh.jsf.core.utils.Extensions.longVal;

public class SQLSourceDataProvider {
    private static SQLSourceDataProvider instance;
    private static final Object sync = new Object();
    private HashMap<Long, User> storedUsersById = new HashMap<>();
    private HashMap<String, User> storedUsersByNames = new HashMap<>();
    private Random random = new Random(99999);
    private MysqlDataSource dataSource = new MysqlDataSource();
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SQLSourceDataProvider.class);
    private ArrayList<Long> allIds = new ArrayList<>();

    public static SQLSourceDataProvider getInstance() {
        if (instance == null) {
            synchronized (sync) {
                instance = new SQLSourceDataProvider();
            }
        }
        return instance;
    }

    private SQLSourceDataProvider() {
        AppProperties properties = AppProperties.getInstance();
        dataSource.setUser(properties.getLocaltwitterUser());
        dataSource.setPassword(properties.getLocaltwitterPass());
        dataSource.setServerName(properties.getLocaltwitterHost());
        dataSource.setDatabaseName(properties.getLocaltwitterDb());
        query("select id from `data`", resultSet -> {
            try {
                allIds.add(resultSet.getLong(1));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void query(String query, Action<ResultSet> action) {
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                action.run(rs);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    public ArrayList<Long> getFollowersIDs(long userId) {
        Var<ArrayList<Long>> results = new Var<>(new ArrayList<>());
        query("select * from `data` where id = " + userId, rs -> {
            try {
                ArrayList<Long> friendsList = new ArrayList<>();
                String friends = rs.getString(10);
                JSONArray jsonArray = new JSONArray(friends);
                for (Object friendId : jsonArray) {
                    long id = longVal(friendId.toString(), -1);
                    if (id != -1 && allIds.contains(id)) {
                        friendsList.add(longVal(friendId.toString()));
                    }
                }
                results.set(friendsList);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return results.get();
    }


    public Node fetch(long twitterId, Node node) {
        Var<Node> result = new Var<>();
        query("select * from `data` where id = " + twitterId, rs -> {
            try {
                node.setId(rs.getLong(1));
                node.setCreatedAt(new Date());
                node.set("ScreenName", rs.getString(2));
                node.set("Email", "samira");
                node.set("AccountName", rs.getString(2));
                node.set("Name", rs.getString(2));
                node.set("CreatedAt", new Date());
                node.set("Description", "none");
                node.set("Lang", rs.getString(7));
                node.set("Location", "USA");
                node.set("FollowersCount", rs.getString(5));
                node.set("FriendsCount", rs.getString(6));
                node.set("Community", rs.getString(3));
                result.set(node);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return result.get();
    }

}
