package dynamicore.xc_middlewares;

import com.arafeh.jsf.core.utils.Var;
import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.Static_Settings;
import dynamicore.Static_Settings.Filters;
import dynamicore.input.middlewares.AfterScanFilterMiddleware;
import dynamicore.input.node.InputNode;

import java.util.List;

public class LocationFilter extends AfterScanFilterMiddleware {
    public LocationFilter() {

    }

    @Override
    public Boolean accepted(Project project, ProjectDataSource dataSource, Node node, InputNode iNode, int currentLevel) {
        if (!dataSource.settingsOf(Filters.LOCATION, currentLevel).isPresent()) return null;
        List<String> locations = dataSource.settingsOf(Filters.LOCATION, currentLevel).get().asStringList();
        for (String loc : locations) {
            if (node.get("Location", "").contains(loc)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Class<? extends AfterScanFilterMiddleware> describe() {
        return LocationFilter.class;
    }
}
