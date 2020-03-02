package dynamicore.xc_middlewares;

import com.arafeh.jsf.core.utils.Var;
import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.Static_Settings;
import dynamicore.input.middlewares.AfterScanFilterMiddleware;
import dynamicore.input.node.InputNode;

import java.util.List;

import static com.arafeh.jsf.core.utils.Extensions.longVal;

public class MinFriendsFilter extends AfterScanFilterMiddleware {

    public MinFriendsFilter() {

    }

    @Override
    public Boolean accepted(Project project, ProjectDataSource dataSource, Node node, InputNode iNode, int currentLevel) {
        if (!dataSource.settingsOf(Static_Settings.Filters.MIN_FRENDS, currentLevel).isPresent()) return null;

        long cond = dataSource.settingsOf(Static_Settings.Filters.MIN_FRENDS, currentLevel).get().asLong();
        long friends = longVal(node.get("FriendsCount").orElse("0").toString(), 0);
        return friends >= cond;
    }

    @Override
    public Class<? extends AfterScanFilterMiddleware> describe() {
        return MinFriendsFilter.class;
    }
}
