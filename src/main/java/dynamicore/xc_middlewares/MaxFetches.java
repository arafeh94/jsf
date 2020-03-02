package dynamicore.xc_middlewares;

import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.NodeType;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import com.arafeh.jsf.service.LoggingService;
import dynamicore.Static_Settings;
import dynamicore.input.middlewares.AfterScanFilterMiddleware;
import dynamicore.input.node.InputNode;
import org.bson.diagnostics.Loggers;
import org.slf4j.Logger;

import javax.ws.rs.POST;

import static com.arafeh.jsf.core.utils.Extensions.intVal;
import static com.arafeh.jsf.core.utils.Extensions.isNullOrEmpty;

public class MaxFetches extends AfterScanFilterMiddleware {
    public MaxFetches() {

    }

    /**
     * MaxFetches: limit the number of scanned nodes of the parent, if
     * the max follower threshold is reached every child from the parent node now
     * wont be accept accepted and will be presented as a blocked node.
     * - A scanned node is a node that has children
     * - the max threshold is reached when the number of scanned node bigger than
     * the max follower condition
     */
    @Override
    public Boolean accepted(Project project, ProjectDataSource dataSource, Node node, InputNode iNode, int currentLevel) {
        dataSource.settingsOf(Static_Settings.Filters.MAX_FETCHES, currentLevel).ifPresent(s -> {
            String condString = s.asString();
            if (isNullOrEmpty(condString)) return;
            long fetchedCount = iNode.parent().children().stream().filter(InputNode::isFetched).count();
            int cond;
            if (condString.endsWith("%")) {
                cond = intVal(condString.split("%")[0], 0);
                cond = (iNode.parent().children().size() * cond) / 100;
            } else {
                cond = intVal(condString, 0);
            }
            if (cond == 0) return;
            LoggingService.log("Max Fetches:", fetchedCount, " fetched of", cond);
            //if scanning limit reached make all the unscanned followers node dead
            if (fetchedCount >= cond) {
                iNode.parent().children().stream().filter(n -> !n.isFetched())
                        .forEach(n -> {
                            if (n.getType() == NodeType.POST) {
                                n.setScannable(false);
                            } else {
                                n.setDead(true);
                                n.setReason(n.getType() + " : MxF Reached");
                            }
                        });
            }
        });
        return null;
    }

    @Override
    public Class<? extends AfterScanFilterMiddleware> describe() {
        return MaxFetches.class;
    }

}
