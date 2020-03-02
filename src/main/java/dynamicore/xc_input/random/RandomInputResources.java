package dynamicore.xc_input.random;

import com.arafeh.jsf.core.utils.RandomExtension;
import com.arafeh.jsf.service.LoggingService;
import com.arafeh.jsf.service.TaskManagement;

import java.util.ArrayList;

import static com.arafeh.jsf.core.utils.Extensions.random;

public class RandomInputResources {
    public static RandomInputNode graph(int graphSeed, int maxDepth) {
        LoggingService.log("creating graph started");
        RandomExtension randomExtension = random(graphSeed);
        RandomInputNode root = new RandomInputNode(0, 0, 0);
        fill(root, 1, randomExtension, maxDepth);
        LoggingService.log("creating graph ended");
        return root;
    }

    private static void fill(RandomInputNode parent, int depth, RandomExtension random, int maxDepth) {
        if (depth == maxDepth) return;
        LoggingService.log("depth: " + depth);
        for (int i = 0; i < random.nextInt(0, 20); i++) {
            RandomInputNode child = new RandomInputNode(random.nextLong(0), depth, i);
            fill(child, depth + 1, random, maxDepth);
            parent.link(child);
        }
    }

    public static class RandomInputNode {
        public long id;
        public ArrayList<RandomInputNode> linked = new ArrayList<>();
        public int depth;
        public int pos;

        public RandomInputNode(long id, int depth, int pos) {
            this.id = id;
            this.depth = depth;
            this.pos = pos;
        }

        public void link(RandomInputNode node) {
            this.linked.add(node);
        }

        public RandomInputNode navigate(long id) {
            if (this.id == id) return this;
            for (RandomInputNode link : this.linked) {
                RandomInputNode nav = link.navigate(id);
                if (nav != null) return nav;
            }
            return null;
        }
    }
}
