package dynamicore.input.middlewares;

import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.input.node.InputNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static dynamicore.input.middlewares.Event.NodeFetched;

@SuppressWarnings("ALL")
public abstract class ScanFilterMiddleware implements DataInputMiddleware {
    private static Logger LOG = LoggerFactory.getLogger(ScanFilterMiddleware.class);

    public ScanFilterMiddleware() {

    }

    @Override
    public void event(Event event, Object[] params) {
        Project project = (Project) params[0];
        ProjectDataSource dataSource = project.getDataSource();
        InputNode lastNode = (InputNode) params[2];
        Optional<Node> node = Optional.empty();
        if (params.length > 3) node = (Optional<Node>) params[3];

        if (lastNode == null) {
            LOG.error("something wrong, lastNode:{}, node:{}", lastNode, node);
            return;
        }
        if (lastNode.location().isRoot()) return;
        if (ignoreFirstLevel() && lastNode.location().getDepth() == 1) return;
        if (ignoreIfDead() && lastNode.isDead()) return;
        Boolean accepted = accepted(project, dataSource, node.orElse(null),
                lastNode, lastNode.location().getDepth());
        if (accepted == null) return;
        if (!accepted) {
            String reason = String.format("Refused by %s", describe().getSimpleName());
            lastNode.setDead(true);
            lastNode.setReason(reason);
        }
    }

    @Override
    public void event(String event, Object[] params) {

    }

    public boolean ignoreIfDead() {
        return true;
    }

    public boolean ignoreFirstLevel() {
        return true;
    }

    public abstract Boolean accepted(Project project, ProjectDataSource dataSource, Node node, InputNode iNode, int currentLevel);

    public abstract Class<? extends ScanFilterMiddleware> describe();
}
