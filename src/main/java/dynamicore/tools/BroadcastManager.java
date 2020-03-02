package dynamicore.tools;

import com.arafeh.jsf.core.utils.HashMap;
import com.arafeh.jsf.model.Project;
import dynamicore.input.middlewares.DataInputMiddleware;

import java.util.ArrayList;
import java.util.List;

import static com.arafeh.jsf.core.utils.Extensions.isNullOrEmpty;

public class BroadcastManager {
    private static HashMap<Project, Broadcaster> broadcasters = new HashMap<>();

    public static Broadcaster getInstance(Project context) {
        return broadcasters.get(context);
    }

    public static void register(Project project, Broadcaster broadcaster) {
        broadcasters.put(project, broadcaster);
    }

    public static void release(Project project) {
        broadcasters.remove(project);
    }

    public static Broadcaster create(List<DataInputMiddleware> middlewares) {
        return new Broadcaster(middlewares);
    }

    public static class Broadcaster {
        List<DataInputMiddleware> middlewares;

        public Broadcaster(List<DataInputMiddleware> middlewares) {
            this.middlewares = middlewares;
        }

        public void broadcast(String eventName, Object... params) {
            if (isNullOrEmpty(middlewares)) return;
            for (DataInputMiddleware middleware : middlewares) {
                middleware.event(eventName, params);
            }
        }
    }
}
