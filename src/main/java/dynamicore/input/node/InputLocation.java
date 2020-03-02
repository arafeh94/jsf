package dynamicore.input.node;

import java.io.Serializable;

public class InputLocation implements Serializable {
    public static InputLocation START = new InputLocation(0, 0);
    private final int depth;
    private final int pos;

    public InputLocation(int depth, int pos) {
        this.depth = depth;
        this.pos = pos;
    }

    public int getDepth() {
        return depth;
    }

    public int getPos() {
        return pos;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof InputLocation) {
            InputLocation other = (InputLocation) obj;
            return other.depth == this.depth && other.pos == this.pos;
        }
        return false;
    }

    public boolean isRoot() {
        return this.depth == 0;
    }

    @Override
    public String toString() {
        return String.format("(depth: %d,pos: %d)", depth, pos);
    }
}
