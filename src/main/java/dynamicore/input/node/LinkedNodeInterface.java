package dynamicore.input.node;

import com.arafeh.jsf.core.protocols.Action;
import com.arafeh.jsf.core.protocols.Function;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface LinkedNodeInterface<T> {

    String getCompositeId();

    void link(T node);

    void linkAll(Collection<T> nodes);

    List<T> parents();

    T parent();

    List<T> children();

    T child(int pos);

    List<T> siblings();

    List<T> neighborhood();

    T root();

    T navigate(long id);

    boolean hasChildren();

    boolean exists(T node);

    void each(Action<T> action);

    int size();

    int size(Function<Boolean, InputNode> where);
}
