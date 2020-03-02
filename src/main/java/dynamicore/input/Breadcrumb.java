package dynamicore.input;

import com.arafeh.jsf.core.data.Preferences;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import dynamicore.input.node.InputLocation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * first level is 0,0 to it's technically useless to save it
 */
public class Breadcrumb extends LinkedList<InputLocation> {
    private long projectId;

    public Breadcrumb(long projectId) {
        this.projectId = projectId;
    }


    /**
     * suppose that you are in a graph, and you are the first node,
     * how to get to the 5th node in the 4th level ?
     * hard eh? you know that you need the node at position 4 in the 5th level,
     * but guess what, you don't know on which branch in the 5th
     * level the forth node you need is in to. So how to fix?
     * breadcrumb save the whole path so we can simulate it to give us the
     * selected branch in each level,
     * for eg. to get to 5th level, at level 1 go to 3rd node, at level 2 go
     * to 4rd node, at level 3 go to 1st
     * node and so on until you reach the 5th level
     * <p>
     * since simulation is called so much, a backup mechanism is used
     * to ease the execution of function
     *
     * @return
     */
    public LinkedList<Integer> simulate(int from) {
        if (Breadcrumb.this.isEmpty()) return new LinkedList<>();
        LinkedList<Integer> loc = new LinkedList<>();
        for (InputLocation location : Breadcrumb.this) {
            while (!loc.isEmpty() && loc.size() > location.getDepth()) {
                loc.removeLast();
            }
            loc.add(location.getPos());
        }
        while (from != 0) {
            from -= 1;
            loc.remove();
        }

        return loc;
    }

    public LinkedList<Integer> simulate() {
        return this.simulate(0);
    }

    @Override
    public boolean add(InputLocation inputLocation) {
        return super.add(inputLocation);
    }

    public void save() {
        Preferences.getInstance().editor().serialise("breadcrumb" + projectId, this).commit();
    }

    /**
     * Gson return save item in list as @{LinkedTreeMap}
     * this is why we need to transform them to LinkedList<InputLocation>
     *
     * @param projectId
     * @return
     */
    public static Breadcrumb load(long projectId) {
        Breadcrumb breadcrumb = new Breadcrumb(projectId);
        String txt = Preferences.getInstance().get("breadcrumb" + projectId);
        List<LinkedTreeMap> list = new Gson().fromJson(txt, LinkedList.class);
        if (list != null) {
            for (LinkedTreeMap linkedTreeMap : list) {
                breadcrumb.add(new InputLocation(
                        ((Double) linkedTreeMap.get("depth")).intValue(),
                        ((Double) linkedTreeMap.get("pos")).intValue()
                ));
            }
        }
        return breadcrumb;
    }


    public static void purge() {
        Preferences.Editor editor = Preferences.getInstance().editor();
        for (Object o : Preferences.getInstance().keys()) {
            if (o.toString().startsWith("breadcrumb")) {
                editor.remove(o.toString());
            }
        }
        editor.commit();
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder();
        HashMap<Integer, Integer> map = new HashMap<>();
        for (InputLocation location : this) {
            int depth = location.getDepth();
            if (!map.containsKey(depth)) {
                map.put(depth, 0);
            }
            int newOne = map.get(depth) + 1;
            map.put(depth, newOne);
        }
        for (Integer integer : map.keySet()) {
            st.append("Level: ").append(integer).append(", Count: ").append(map.get(integer)).append("\n");
        }
        return st.toString();
    }
}
