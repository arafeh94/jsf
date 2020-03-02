package dynamicore.input;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.bll.RelationNodeBll;
import com.arafeh.jsf.core.utils.Func;
import com.arafeh.jsf.model.*;
import com.arafeh.jsf.service.LoggingService;
import com.google.gson.Gson;
import dynamicore.input.middlewares.DataInputMiddleware;
import dynamicore.input.middlewares.Event;
import dynamicore.input.node.InputLocation;
import dynamicore.input.node.InputNode;
import dynamicore.input.node.NodeManager;
import dynamicore.navigator.Navigator;
import dynamicore.tools.BroadcastManager;

import java.util.List;
import java.util.Optional;

import static com.arafeh.jsf.core.utils.Extensions.*;

public abstract class DataInputBase implements DataInputInterface {
    private static InnateLogger logger = new InnateLogger();

    private static final int UPDATE_GRAPH_STAMP = 50;
    protected Project project;
    protected NodeBll nodeBll;
    protected GraphNodeBll graphNodeBll;
    protected ProjectBll projectBll;
    protected RelationNodeBll relationNodeBll;
    protected List<DataInputMiddleware> middlewares;
    private boolean stopped = false;
    protected ProjectDataSource dataSource;
    protected Gson gson;
    protected Breadcrumb breadcrumb;
    protected InputNode root;
    protected int iterations = 0;


    @Override
    public void init(Project project, NodeBll nodes, GraphNodeBll graphNodes, ProjectBll projectBll, RelationNodeBll relationNodeBll, List<DataInputMiddleware> middlewares) throws DataSourceNotRegisteredException {
        logger.log("init vars");
        this.gson = new Gson();
        this.project = project;
        this.nodeBll = nodes;
        this.graphNodeBll = graphNodes;
        this.projectBll = projectBll;
        this.relationNodeBll = relationNodeBll;
        this.middlewares = middlewares;
        logger.log("init datasource");
        this.dataSource = project.getDataSource();
        logger.log("init breadcrumb");
        this.breadcrumb = Breadcrumb.load(this.project.getId());
        logger.log("generating root");
        root = generateRoot();
    }


    public void observe(Navigator navigator) {
        BroadcastManager.register(project, BroadcastManager.create(middlewares));
        broadcast(Event.ObservationStarted, project);
        logger.log("checking for lost recovery");
        recover();
        logger.log("scanning root - started");
        scan(root);
        logger.log("scanning root - finished", root.toString());
        logger.log("saving root relations - started");
        relationNodeBll.saveNodeNeo4j(root);
        relationNodeBll.saveNode(root);
        logger.log("saving root relations - finished");
        for (InputNode seed : root.children()) {
            logger.log("seed", seed, "started");
            navigate(navigator, seed);
            logger.log("seed", seed, "finished");
        }
        if (projectBll.executable(project)) {
            logger.log("process finished");
            projectBll.set(project);
            BroadcastManager.release(project);
        }
    }

    private void recover() {
        if (relationNodeBll.graphExists(this.project.getId())) {
            logger.log("lost graph found, recovery started");
            this.root = relationNodeBll.getInputGraph(this.project, this.nodeBll);
            logger.log("lost graph recovery ended");
        } else {
            logger.log("no lost to recover");
            logger.log("building new graph");
        }
    }

    public void navigate(Navigator navigator, InputNode seed) {
        navigator.reset(seed);
        InputNode lastNode = seed;
        do {
            iterations++;
            logger.log("Iteration Number", iterations);
            broadcast(Event.NodeWillFetch, project, describe(), lastNode);
            if (lastNode.hasChildren()) {
                logger.log("node already scanned, skip:", lastNode.getId() + "");
                navigator.failedFetch();
            } else if (lastNode.isDead()) {
                logger.log("node is dead because", lastNode.getReason(), "skip:", lastNode.getId() + "");
                navigator.failedFetch();
            } else if (!lastNode.isScannable()) {
                logger.log("node is not scannable", lastNode.getId());
                navigator.failedFetch();
            } else {
                logger.log("scanning start: ", lastNode.getId() + "");
                scan(lastNode);
                logger.log("scanning finished: ", lastNode.toString());
                logger.log("child count results: ", lastNode.children().size());
                logger.log("caching node");
                if (lastNode.hasChildren()) {
                    logger.log("fetching node - started");
                    Optional<Node> node = retrieveOrFetch(lastNode);
                    logger.log("fetching node - finished", node.map(Node::toString).orElse("bad request"));
                    broadcast(Event.NodeFetched, project, describe(), lastNode, node);
                    if (lastNode.isDead()) {
                        logger.log("fetched node marked dead because: ", lastNode.getReason());
                        lastNode.children().clear();
                    } else {
                        long time = System.nanoTime();
                        logger.log("saving relations to neo4j - started");
                        relationNodeBll.saveNodeNeo4j(lastNode);
                        time = System.nanoTime() - time;
                        logger.log("saving relations to neo4j - finished", "took:", time / 1000000000, "s");
                    }
                    cache(node, lastNode);
                    navigator.successFetch();
                    broadcast(Event.UpdateSaved, project, root);
                } else {
                    navigator.failedFetch();
                    logger.log("node caching failed with reason:", "don't have any children");
                }
            }
            breadcrumb.add(lastNode.location());
            broadcast(Event.BreadCrumbUpdated, project, breadcrumb);
            lastNode = navigator.next(lastNode);
            broadcast(Event.LastNodeUpdated, project, lastNode);
            stopped = !projectBll.executable(project);
            if (stopped) logger.log("project", project.getName(), "stopped");
        } while (lastNode != null && !stopped);
    }


    private Optional<Node> retrieveOrFetch(InputNode inputNode) {
        Optional<Node> node = nodeBll.get(inputNode.getId(), describe().getName());
        if (!node.isPresent()) {
            node = Optional.ofNullable(fetch(inputNode));
        }
        inputNode.setFetched(true);
        return node;
    }

    private void cache(Optional<Node> node, InputNode lastNode) {
        node.ifPresent(n -> {
            if (!lastNode.location().equals(InputLocation.START)
                    && isNullOrEmpty(lastNode.getJson())) {
                lastNode.setJson(gson.toJson(n));
            }
            nodeBll.set(n);
        });
        NodeManager.putLastNodeId(project, lastNode.getId());
    }


    private InputNode generateRoot() {
        broadcast(Event.CreateMainGraph, project, describe());
        long rootId = random().UUIDLong();
        this.dataSource.setGraphId(String.valueOf(rootId));
        this.project.setDataSource(this.dataSource);
        projectBll.set(project);
        broadcast(Event.MainGraphFetched, root);
        return createInputNode(rootId, NodeType.ROOT, "root", 0, 0);
    }

    private void broadcast(Event event, Object... params) {
        if (isNullOrEmpty(middlewares)) return;
        for (DataInputMiddleware middleware : middlewares) {
            middleware.event(event, params);
        }
    }


    protected void pause() {
        logger.log("warning!", ProjectState.RESOURCES_EXHAUSTED);
        project.setState(ProjectState.RESOURCES_EXHAUSTED);
        projectBll.set(project);
        stopped = true;
    }

    protected Node createNode(NodeType type) {
        return new Node(type, describe().getName());
    }

    /**
     * scan method give us all the required information to continue the
     * observations, the most important information it that it must tell us about
     * the linked node to the given node, and other information to help us know
     * more about the current node
     *
     * @param inputNode
     */
    public abstract void scan(InputNode inputNode);

    /**
     * different from scan method who must return followers or friends,
     * fetch method must give additional information about the node, for example
     * node name...
     *
     * @param lastNode
     * @return
     */
    protected abstract Node fetch(InputNode lastNode);

    protected InputNode createInputNode(long id, NodeType type, String json, int depth, int pos) {
        String source = generateSource(project, describe());
        return InputNode.create(id, project.getId(), source, json, type, depth, pos);
    }

    public static InputNode createInputNode(Project project, Class<? extends DataInputBase> inputSource, long id, NodeType type, String json, int depth, int pos) {
        String source = generateSource(project, inputSource);
        return InputNode.create(id, project.getId(), source, json, type, depth, pos);
    }

    public static String generateSource(Project project, Class<? extends DataInputBase> cls) {
        return project.getId() + ":" + cls.getSimpleName();
    }

    private static class InnateLogger {

        public void log(Object... msg) {
            LoggingService.log(build(msg));
        }

        private String build(Object... msg) {
            StringBuilder builder = new StringBuilder();
            for (Object s : msg) {
                builder.append(String.valueOf(s)).append(" ");
            }
            return builder.toString();
        }
    }


}
