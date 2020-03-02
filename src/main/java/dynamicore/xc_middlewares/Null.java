package dynamicore.xc_middlewares;

import com.arafeh.jsf.core.utils.Var;
import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.Static_Settings;
import dynamicore.input.middlewares.AfterScanFilterMiddleware;
import dynamicore.input.node.InputNode;

import java.util.List;

public class Null extends AfterScanFilterMiddleware {
    public Null() {

    }

    @Override
    public Boolean accepted(Project project, ProjectDataSource dataSource, Node node, InputNode iNode, int currentLevel) {
        return true;
    }

    @Override
    public Class<? extends AfterScanFilterMiddleware> describe() {
        return Null.class;
    }
}
