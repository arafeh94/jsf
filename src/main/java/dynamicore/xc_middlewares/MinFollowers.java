package dynamicore.xc_middlewares;

import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.NodeType;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.Static_Settings;
import dynamicore.input.middlewares.AfterScanFilterMiddleware;
import dynamicore.input.node.InputNode;

import static com.arafeh.jsf.core.utils.Extensions.longVal;

public class MinFollowers extends AfterScanFilterMiddleware {
    public MinFollowers() {

    }

    @Override
    public Boolean accepted(Project project, ProjectDataSource dataSource, Node node, InputNode iNode, int currentLevel) {
        if (!iNode.getType().equals(NodeType.FOLLOWER)) return null;

        if (!dataSource.settingsOf(Static_Settings.Filters.MIN_FOLLOWERS_THRESHOLD, currentLevel)
                .isPresent()) return null;

        int cond = dataSource.settingsOf(Static_Settings.Filters.MIN_FOLLOWERS_THRESHOLD, currentLevel).get().asInt();
        long followers = longVal(node.get("FollowersCount").orElse("0").toString(), 0);
        return followers >= cond;
    }

    @Override
    public Class<? extends AfterScanFilterMiddleware> describe() {
        return MinFollowers.class;
    }

    @Override
    public boolean ignoreIfDead() {
        return true;
    }

    @Override
    public boolean ignoreFirstLevel() {
        return true;
    }
}
