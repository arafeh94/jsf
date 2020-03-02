package dynamicore.input;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.bll.RelationNodeBll;
import com.arafeh.jsf.model.Project;
import dynamicore.input.middlewares.DataInputMiddleware;
import dynamicore.navigator.Navigator;

import java.util.ArrayList;
import java.util.List;

public interface DataInputInterface {
    void observe(Navigator navigator) throws DataSourceNotRegisteredException;

    void init(Project project, NodeBll nodes, GraphNodeBll graphNodes, ProjectBll projectBll, RelationNodeBll relationNodeBll, List<DataInputMiddleware> middlewares) throws DataSourceNotRegisteredException;

    DataSourceState getStats();

    Class<? extends DataInputBase> describe();
}
