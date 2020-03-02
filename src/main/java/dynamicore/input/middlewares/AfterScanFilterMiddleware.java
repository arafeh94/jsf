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

@SuppressWarnings("ALL")
public abstract class AfterScanFilterMiddleware extends ScanFilterMiddleware {
    private static Logger LOG = LoggerFactory.getLogger(AfterScanFilterMiddleware.class);

    public AfterScanFilterMiddleware() {

    }

    @Override
    public void event(Event event, Object[] params) {
        if (event != NodeFetched) return;
        super.event(event, params);
    }


    @Override
    public void event(String event, Object[] params) {
        super.event(event, params);
    }

}
