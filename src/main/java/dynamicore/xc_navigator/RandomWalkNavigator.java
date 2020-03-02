package dynamicore.xc_navigator;

import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSourceSettings;
import com.arafeh.jsf.service.LoggingService;
import dynamicore.Static_Settings;
import dynamicore.input.DataInputBase;
import dynamicore.input.node.InputLocation;
import dynamicore.input.node.InputNode;
import dynamicore.navigator.Navigator;
import dynamicore.navigator.NavigatorBase;
import org.bson.diagnostics.Loggers;

import static com.arafeh.jsf.core.utils.Extensions.random;

public class RandomWalkNavigator extends NavigatorBase {
    private float forwardWeight;
    private int maxLevel;
    private int maxCalls;
    protected int currentCalls;

    @Override
    public void init(Project project, Class<? extends DataInputBase> dataSourceInterface) {
        super.init(project, dataSourceInterface);
        this.forwardWeight = dataSource.settingsOf(Static_Settings.RandomWalkSettings.FORWARD_WEIGHT).stream().findFirst().orElse(new ProjectDataSourceSettings("", "0.5", "")).asFloat(0.5f);
        this.maxLevel = dataSource.settingsOf(Static_Settings.Filters.MAX_LEVEL).stream().findFirst().orElse(new ProjectDataSourceSettings("", "0", "")).asInt();
        this.maxCalls = dataSource.settingsOf(Static_Settings.RandomWalkSettings.ITERATIONS).stream().findFirst().orElse(new ProjectDataSourceSettings("", "5000", "")).asInt();

    }

    @Override
    public void successFetch() {
        super.successFetch();
        currentCalls += 1;
    }

    @Override
    public void failedFetch() {
        super.failedFetch();
        currentCalls += 1;
    }

    @Override
    public InputNode next(InputNode last) {
        if (currentCalls >= maxCalls) return null;
        boolean isForward = isForward(last);
        LoggingService.log("location", last.location(), ",", "forward", isForward, ",", "parent", last.parent().location(), ",", "iterations", currentCalls);
        InputNode results;
        if (isForward && last.hasChildren()) {
            results = last.child(nextPos(last.children().size()));
        } else {
            InputNode parent = last.parent();
            InputNode grand = last.parent().parent();
            if (parent != null) {
                if (grand != null && !grand.location().equals(InputLocation.START)) {
                    results = grand.child(nextPos(grand.children().size()));
                } else {
                    results = parent.child(nextPos(parent.children().size()));
                }
            } else {
                results = last.child(nextPos(last.children().size()));
            }
        }
        return results;
    }

    @Override
    public void reset(InputNode start) {
        currentCalls = start.size(InputNode::hasChildren);
    }

    private boolean isForward(InputNode node) {
        if (node.location().equals(InputLocation.START)) {
            return true;
        }
        if (node.location().getDepth() == 1) {
            return true;
        }
        if (maxLevel != 0 && node.location().getDepth() >= maxLevel) {
            return false;
        }
        return random().nextBool(forwardWeight);
    }

    private int nextPos(int max) {
        if (max == 0) return 0;
        return random().nextInt(0, max);
    }

    @Override
    public Class<? extends Navigator> describe() {
        return RandomWalkNavigator.class;
    }
}
