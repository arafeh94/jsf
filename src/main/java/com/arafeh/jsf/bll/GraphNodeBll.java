package com.arafeh.jsf.bll;

import com.arafeh.jsf.core.utils.HashMap;
import com.arafeh.jsf.core.utils.Pair;
import com.arafeh.jsf.dal.GraphNodeDAL;
import com.arafeh.jsf.dal.datasource.Neo4jDataSource;
import com.arafeh.jsf.model.GraphNode;
import com.arafeh.jsf.service.LoggingService;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.arafeh.jsf.core.utils.Extensions.floatVal;
import static com.arafeh.jsf.core.utils.Extensions.random;
import static org.neo4j.ogm.session.Utils.map;

@Singleton
@LocalBean
public class GraphNodeBll implements Serializable {
    private static GraphNodeDAL graphNodes;

    public enum Algos {
        PAGE_RANK("algo.pageRank.stream", "score", "-[:linked]->"),
        EIGENVECTOR_CENTRALITY("algo.pageRank.stream", "score", "-[:linked]->"),
        DEGREE_CENTRALITY("algo.degree.stream", "score", "<-[:linked]-");

        public String call;
        public String column;
        public String link;

        Algos(String call, String column, String link) {
            this.call = call;
            this.column = column;
            this.link = link;
        }
    }

    public GraphNodeBll() {
    }

    @PostConstruct
    public void init() {
        if (graphNodes == null) {
            graphNodes = new GraphNodeDAL();
        }
    }

    public Long generateId() {
        return random().UUIDLong();
    }

    public GraphNode get(String parentGraphNodeId, GraphNode graphNode) {
        Optional<GraphNode> result = graphNodes.find(parentGraphNodeId);
        return result.orElse(graphNode);
    }

    public List<GraphNode> all() {
        return graphNodes.all();
    }


    public ArrayList<Pair<String, Double>> pageRank(long projectId) {
        ArrayList<Pair<String, Double>> results = new ArrayList<>();
        Neo4jDataSource.getInstance().client().openSession().query(analyse(Algos.PAGE_RANK, projectId), map()).forEach(res -> {
            results.add(new Pair<>((String) res.get("id"), (Double) res.get("score")));
        });
        return results;
    }

    public ArrayList<Pair<String, Double>> degreeCentrality(long projectId) {
        ArrayList<Pair<String, Double>> results = new ArrayList<>();
        String query = analyse(Algos.DEGREE_CENTRALITY, projectId);
        LoggingService.log("executing analysis query", query);
        Neo4jDataSource.getInstance().client().openSession().query(analyse(Algos.DEGREE_CENTRALITY, projectId), map()).forEach(res -> {
            results.add(new Pair<>((String) res.get("id"), (Double) res.get("score")));
        });
        return results;
    }

    public ArrayList<Pair<String, Double>> eigenvectorCentrality(long projectId) {
        ArrayList<Pair<String, Double>> results = new ArrayList<>();
        Neo4jDataSource.getInstance().client().openSession().query(analyse(Algos.EIGENVECTOR_CENTRALITY, projectId), map()).forEach(res -> {
            results.add(new Pair<>((String) res.get("id"), (Double) res.get("score")));
        });
        return results;
    }

    public String analyse(Algos algos, long projectId, HashMap<String, Object> properties) {
        if (properties == null) properties = new HashMap<>();
        properties.put("graph", "cypher");

        return String.format("CALL %s(\n" +
                "  'MATCH (g:GraphNode) where g.projectId = \"%d\" RETURN id(g) as id',\n" +
                "  'MATCH (g1:GraphNode)%s(g2:GraphNode) RETURN id(g1) as source, id(g2) as target',\n" +
                "  {%s}\n" +
                ")\n" +
                "YIELD nodeId, %s \n" +
                "RETURN algo.getNodeById(nodeId).id AS id, score\n" +
                "ORDER BY score DESC", algos.call, projectId, algos.link, propertiesToString(properties), algos.column);
    }

    public String analyse(Algos algos, long projectId) {
        return analyse(algos, projectId, null);
    }

    private String propertiesToString(HashMap<String, Object> properties) {
        StringBuilder query = new StringBuilder();
        properties.forEach((key, val) -> {
            query.append(key).append(":").append("'").append(String.valueOf(val)).append("'").append(",");
        });
        return query.subSequence(0, query.length() - 1).toString();
    }


    public Optional<GraphNode> get(String id) {
        return graphNodes.find(id);
    }

    public void save(GraphNode graphNode) {
        graphNodes.add(graphNode);
    }

    public void clear() {
        graphNodes.clear();
    }

    public void clean() {
        graphNodes.query(session -> {
            String query = "MATCH (n:GraphNode) where n.isDead = true \n" +
                    "OPTIONAL MATCH (n)-[r]-() \n" +
                    "delete n,r";
            session.query(query, map());
            return new ArrayList<>();
        });
    }

    public void delete(GraphNode root) {
        graphNodes.query(session -> {
            String query = "MATCH (n:GraphNode)-[*]-(allRelatedNodes) " +
                    "WHERE n.id = \"" + root.getId() + "\" \n" +
                    "DETACH DELETE n, allRelatedNodes;";
            session.query(query, map());
            return new ArrayList<>();
        });
    }
}
