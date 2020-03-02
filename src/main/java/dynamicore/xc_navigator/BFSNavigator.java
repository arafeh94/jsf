package dynamicore.xc_navigator;

import dynamicore.input.node.InputNode;
import dynamicore.navigator.Navigator;
import dynamicore.navigator.NavigatorBase;

import java.util.ArrayList;
import java.util.List;

public class BFSNavigator extends NavigatorBase {

    @Override
    public InputNode next(InputNode last) {
        if (last.location().getDepth() == 1) {
            if (last.hasChildren()) {
                return last.children().get(0);
            } else {
                return null;
            }
        } else {
            InputNode selected = nextNode(last);
            if (selected == null) {
                for (InputNode neighbor : last.neighborhood()) {
                    if (neighbor.hasChildren() && !neighbor.isDead()) {
                        return neighbor.child(0);
                    }
                }
                return null;
            }
            return selected;
        }
    }

    public InputNode nextNode(InputNode node) {
        List<InputNode> neighbors = new ArrayList<>(node.neighborhood());
        for (int i = 0; i < neighbors.size(); i++) {
            if (neighbors.get(i).equals(node)) {
                if (i + 1 < neighbors.size()) {
                    return neighbors.get(i + 1);
                }
            }
        }
        return null;
    }

    @Override
    public Class<? extends Navigator> describe() {
        return BFSNavigator.class;
    }
}
