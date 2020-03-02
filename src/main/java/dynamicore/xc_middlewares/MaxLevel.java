package dynamicore.xc_middlewares;

import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.Static_Settings;
import dynamicore.input.middlewares.BeforeScanFilterMiddleware;
import dynamicore.input.node.InputNode;

public class MaxLevel extends BeforeScanFilterMiddleware {

    @Override
    public Boolean accepted(Project project, ProjectDataSource dataSource, Node node, InputNode iNode, int currentLevel) {
        dataSource.settingsOf(Static_Settings.Filters.MAX_LEVEL).stream().findFirst().ifPresent(s -> {
            int settingsMaxLevel = s.asInt();
            boolean scannable = currentLevel <= settingsMaxLevel;
            iNode.setScannable(scannable);
        });
        return null;
    }

    @Override
    public Class<? extends BeforeScanFilterMiddleware> describe() {
        return MaxLevel.class;
    }

}
