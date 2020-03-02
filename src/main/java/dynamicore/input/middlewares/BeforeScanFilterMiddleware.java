package dynamicore.input.middlewares;

import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.input.DataInputBase;
import dynamicore.input.node.InputNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static dynamicore.input.middlewares.Event.NodeFetched;
import static dynamicore.input.middlewares.Event.NodeWillFetch;

/**
 * if node is marked as bead in before scan filter middleware, it wont show in
 * graph as dead node. why? because it wasn't filtered like for e.g. location or
 * language, it was only the project limit.
 */
public abstract class BeforeScanFilterMiddleware extends ScanFilterMiddleware {
    private static Logger LOG = LoggerFactory.getLogger(BeforeScanFilterMiddleware.class);

    public BeforeScanFilterMiddleware() {

    }

    @Override
    public void event(Event event, Object[] params) {
        if (event != NodeWillFetch) return;
        super.event(event, params);
    }

    @Override
    public void event(String event, Object[] params) {
        super.event(event, params);
    }

}
