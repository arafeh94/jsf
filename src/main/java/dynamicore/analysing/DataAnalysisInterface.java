package dynamicore.analysing;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.input.DataInputBase;
import javafx.scene.control.Tab;

public interface DataAnalysisInterface {
    void init(Project project, ProjectDataSource dataSource, ProjectBll projectBll, NodeBll nodeBll, GraphNodeBll graphNodeBll);

    Table analyse();

    Class<? extends DataAnalysisInterface> describe();
}
