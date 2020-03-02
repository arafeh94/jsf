package com.arafeh.jsf.bll;

import com.arafeh.jsf.core.data.Preferences;
import com.arafeh.jsf.core.protocols.Action;
import com.arafeh.jsf.core.utils.*;
import com.arafeh.jsf.core.utils.HashMap;
import com.arafeh.jsf.dal.RelationNodeDAL;
import com.arafeh.jsf.model.*;
import com.arafeh.jsf.service.Neo4jExecutorService;
import com.google.gson.Gson;
import dynamicore.input.node.InputNode;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.arafeh.jsf.core.utils.Extensions.*;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Singleton
@LocalBean
public class RelationNodeBll implements Serializable {
    public static Logger logger = LoggerFactory.getLogger(RelationNodeBll.class);

    private static RelationNodeDAL relationNodes;
    private static RelationNodeBll instance;
    private static Neo4jExecutorService neo4jExecutorService;

    public static RelationNodeBll getInstance() {
        return instance;
    }

    private Gson gson;

    public RelationNodeBll() {
        gson = new Gson();
    }

    @PostConstruct
    public void init() {
        if (relationNodes == null) {
            gson = new Gson();
            relationNodes = new RelationNodeDAL();
            neo4jExecutorService = new Neo4jExecutorService();
            instance = this;
        }
    }

    private void saveNeo4j(InputNode... inputNode) {
        ArrayList<RelationNode> rns = new ArrayList<>();
        for (InputNode node : inputNode) {
            rns.add(asRelationNode(node));
        }
        if (rns.size() > 0) {
            ArrayList<Statement> statements = new ArrayList<>();
            for (RelationNode rn : rns) {
                String query = "merge (parent{id: '" + rn.getLeft() + "', projectId: '" + rn.getProjectId() + "'}) " +
                         "merge (child:" + rn.getCustomType() + "{id: '" + rn.getRight() + "', projectId: '" + rn.getProjectId() + "'}) " +
                        "ON MATCH SET child.text = '" + rn.getText() + "', child.dead = " + rn.isDead() + ", child.type = '" + rn.getType() + "' " +
                        "create unique (parent)<-[r:" + rn.getCustomRelation() + "]-(child)";
                Statement statement = new Statement(query);
                statements.add(statement);
            }
            neo4jExecutorService.execute(statements);
        }
    }

    private void saveNeo4j(List<InputNode> inputNodes) {
        saveNeo4j(inputNodes.toArray(new InputNode[0]));
    }


    public void saveNodeNeo4j(InputNode inputNode) {
        saveNeo4j(inputNode);
        if (inputNode.hasChildren()) saveNeo4j(inputNode.children());
    }

    private void save(InputNode... inputNode) {
        ArrayList<RelationNode> rns = new ArrayList<>();
        for (InputNode node : inputNode) {
            rns.add(asRelationNode(node));
        }

        ArrayList<RelationNode> exists = new ArrayList<>();

        //remove existed relation node from the original array while adding them to the new array
        //updating the id is important for it to actually update in the database
        for (RelationNode relationNode : relationNodes.findBatch(rns)) {
            int index = rns.indexOf(relationNode);
            if (index < 0 || index >= rns.size()) continue;
            RelationNode rn = rns.remove(index);
            rn.setId(relationNode.getId());
            exists.add(rn);
        }
        //the original array now contains all the new nodes only so we can add them all directly
        //remove directly the nodes that are dead
        rns.removeIf(RelationNode::isDead);
        if (rns.size() > 0) {
            relationNodes.addAll(rns);
        }
        //remove all the exists nodes that are dead
        exists.removeIf(relationNode -> !relationNode.isDead());
        if (exists.size() > 0) {
            relationNodes.removeAll(exists);
        }
    }


    private void save(List<InputNode> inputNodes) {
        save(inputNodes.toArray(new InputNode[0]));
    }

    public void saveNode(InputNode inputNode) {
        save(inputNode);
        if (inputNode.hasChildren()) save(inputNode.children());
    }

    public void deleteProjectNode(long projectId) {
        relationNodes.query(collection -> {
            collection.deleteMany(eq("projectId", projectId));
            return new ArrayList<>();
        });
    }

    public InputNode getInputGraph(Project project, NodeBll nodeBll) {
        Var<InputNode> graph = new Var<>();
        relationNodes.findOne(mapOf("projectId", project.getId(), "dead", false)).ifPresent(root -> {
            graph.set(asInputGraph(project, nodeBll, root));
        });
        return graph.get();
    }

    public boolean graphExists(long projectId) {
        return relationNodes.findOne(mapOf("projectId", projectId, "dead", false)).isPresent();
    }


    private InputNode asInputGraph(Project project, NodeBll nodeBll, RelationNode root) {
        HashMap<Long, Node> hashNodes = new HashMap<>();
        logger.info("indexing nodes started");
        nodeBll.all(project.getId()).forEach(node -> hashNodes.put(node.getId(), node));
        logger.info("indexing nodes finished");
        logger.info("collecting relations");
        List<RelationNode> allRelations = relationNodes.find(mapOf("projectId", project.getId()));
        logger.info("{} relations have being found", allRelations.size());
        logger.info("building input graph...");
        return asInputGraph(project, hashNodes, allRelations, root, new ArrayList<>(), 0, 0);
    }

    private InputNode asInputGraph(Project project, HashMap<Long, Node> nodes, List<RelationNode> relationNodes, RelationNode relationNode, ArrayList<RelationNode> passed, int depth, int pos) {
        if (passed.contains(relationNode)) return null;
        passed.add(relationNode);
        Node nodeInfo;
        if (relationNode.getType().equals("ROOT")) {
            nodeInfo = new Node(NodeType.ROOT, project.getDataSource().getSourceName());
        } else {
            nodeInfo = nodes.get(relationNode.getRight());
        }
        InputNode inputNode = InputNode.create(relationNode.getRight(), project.getId(), project.getDataSource().getSource(), tryGson(nodeInfo), NodeType.of(relationNode.getType()), depth, pos);
        ArrayList<InputNode> linked = new ArrayList<>();
        List<RelationNode> children = relationNodes.stream().filter(node -> node.getLeft() == relationNode.getRight()).collect(Collectors.toList());
        int localPos = 0;
        for (RelationNode node : children) {
            InputNode sub = asInputGraph(project, nodes, relationNodes, node, passed, depth + 1, localPos++);
            if (sub != null) linked.add(sub);
        }
        inputNode.linkAll(linked);
        return inputNode;
    }

    /**
     * build neo4j graph
     *
     * @param projectId
     * @return
     */
    public GraphNode buildNeo4jGraph(long projectId) {
        RelationNode root = relationNodes.findOne(mapOf("projectId", projectId, "type", "ROOT")).orElse(null);
        if (root != null) {
            return asNeo4jGraph(projectId, root);
        }
        return null;
    }

    private GraphNode asNeo4jGraph(long projectId, RelationNode root) {
        logger.info("collecting relations");
        List<RelationNode> allRelations = relationNodes.find(mapOf("projectId", projectId));
        logger.info("{} relations have being found", allRelations.size());
        logger.info("building neo4j graph...");
        return asNeo4jGraph(allRelations, root, new ArrayList<>());
    }

    private GraphNode asNeo4jGraph(List<RelationNode> relationNodes, RelationNode relationNode, ArrayList<RelationNode> passed) {
        if (passed.contains(relationNode)) return null;
        passed.add(relationNode);
        GraphNode graphNode = new GraphNode();
        graphNode.setId(String.valueOf(relationNode.getRight()));
        graphNode.setType(NodeType.of(relationNode.getType()));
        ArrayList<GraphNode> linked = new ArrayList<>();
        List<RelationNode> children = relationNodes.stream().filter(node -> node.getLeft() == relationNode.getRight()).collect(Collectors.toList());
        for (RelationNode node : children) {
            GraphNode sub = asNeo4jGraph(relationNodes, node, passed);
            if (sub != null) linked.add(sub);
        }
        graphNode.setLinked(linked);
        return graphNode;
    }

    private String tryGson(Node node) {
        try {
            return gson.toJson(node);
        } catch (Exception e) {
            return "";
        }
    }

    private void forEach(RelationNode root, List<RelationNode> list, Action<RelationNode> action) {
        Set<RelationNode> visited = new LinkedHashSet<>();
        Stack<RelationNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            RelationNode vertex = stack.pop();
            action.run(vertex);
            if (!visited.contains(vertex)) {
                visited.add(vertex);
                List<RelationNode> children = list.stream().filter(node -> node.getLeft() == vertex.getRight()).collect(Collectors.toList());
                for (RelationNode rn : children) {
                    stack.push(rn);
                }
            }
        }
    }


    private void forEach(RelationNode root, Action<RelationNode> action) {
        Set<RelationNode> visited = new LinkedHashSet<>();
        Stack<RelationNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            RelationNode vertex = stack.pop();
            action.run(vertex);
            if (!visited.contains(vertex)) {
                visited.add(vertex);
                for (RelationNode rn : childrenOf(vertex)) {
                    stack.push(rn);
                }
            }
        }
    }


    public List<RelationNode> childrenOf(RelationNode node) {
        return relationNodes.find(mapOf("left", node.getRight()));
    }


    public RelationNode asRelationNode(InputNode inputNode) {
        RelationNode relationNode = new RelationNode();
        long relationId = getSeqId();
        relationNode.setText(inputNode.getText());
        relationNode.setId(relationId);
        relationNode.setProjectId(inputNode.getProjectId());
        relationNode.setCustomRelation(inputNode.getCustomRelation());
        relationNode.setCustomType(inputNode.getCustomType());
        relationNode.setDead(inputNode.isDead());
        relationNode.setType(inputNode.getType().name());
        if (inputNode.parent() != null) {
            relationNode.setLeft(inputNode.parent().getId());
            relationNode.setRight(inputNode.getId());
        } else {
            relationNode.setRight(inputNode.getId());
        }
        return relationNode;
    }

    private long getSeqId() {
        String id = Preferences.getInstance().get("relationLastId");
        if (id == null) id = "1";
        long lastId = Extensions.longVal(id, 1);
        long nextId = lastId + 1;
        Preferences.getInstance().editor().put("relationLastId", String.valueOf(nextId)).commit();
        return lastId;
    }

    public void clear() {
        relationNodes.clear();
    }

    public Optional<RelationNode> rootOf(int projectId) {
        return relationNodes.findOne(mapOf("isRoot", true, "projectId", projectId));
    }

    public long count() {
        return relationNodes.size();
    }

    public ArrayList<RelationNode> getRelationsNeo4j(long projectId, long left) {
        String query = "MATCH ({projectId : " + projectId + ", id : " + left + "})-[*]-(connected)\n" +
                "RETURN connected";
        System.out.println(query);
        StatementResult result = neo4jExecutorService.execute(query);
        ArrayList<RelationNode> relationNodes = new ArrayList<>();
        while (result.hasNext()) {
            Record record = result.next();
            RelationNode relationNode = new RelationNode();
            relationNode.setRight(record.get("id").asLong());
            relationNode.setLeft(left);
            relationNode.setProjectId(projectId);
            relationNodes.add(relationNode);
        }
        return relationNodes;
    }

    public List<RelationNode> getRelations(long projectId, long left) {
        return relationNodes.find(mapOf("projectId", projectId, "left", left));
    }

    public List<RelationNode> getSeeds(long projectId) {
        return relationNodes.find(mapOf("projectId", projectId, "type", "ACCOUNT"));
    }

    public RelationNodeDAL getSource() {
        return relationNodes;
    }

}
