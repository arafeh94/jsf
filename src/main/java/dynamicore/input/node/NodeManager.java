package dynamicore.input.node;

import com.arafeh.jsf.core.data.Preferences;
import com.arafeh.jsf.model.Project;

import static com.arafeh.jsf.core.utils.Extensions.longVal;

public class NodeManager {
    public static void putLastNodeId(Project project, long node) {
        Preferences.getInstance().editor().put(key(project.getName(), "lastNode"), String.valueOf(node)).commit();
    }

    public static long getLastNodeId(Project project) {
        return longVal(Preferences.getInstance().get(key(project.getName(), "lastNode")),-1L);
    }

    private static String key(String... keyParts) {
        StringBuilder stringBuilder = new StringBuilder(descriptor()).append(":");
        for (String keyPart : keyParts) {
            stringBuilder.append(keyPart).append(":");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    private static String descriptor() {
        return NodeManager.class.getName();
    }
}
