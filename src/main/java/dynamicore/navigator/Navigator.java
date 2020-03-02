package dynamicore.navigator;

import com.arafeh.jsf.model.Project;
import dynamicore.input.Breadcrumb;
import dynamicore.input.DataInputBase;
import dynamicore.input.node.InputLocation;
import dynamicore.input.node.InputNode;

import java.io.Serializable;
import java.util.LinkedList;

public interface Navigator extends Serializable {
    InputNode next(InputNode last);

    void reset(InputNode node);

    void init(Project project, Class<? extends DataInputBase> dataSourceInterface);

    void successFetch();

    void failedFetch();

    Class<? extends Navigator> describe();
}
