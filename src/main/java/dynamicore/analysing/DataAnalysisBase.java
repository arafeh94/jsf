package dynamicore.analysing;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;

public abstract class DataAnalysisBase implements DataAnalysisInterface {
    protected Project project;
    protected ProjectDataSource projectDataSource;
    protected GraphNodeBll graphNodeBll;
    protected NodeBll nodeBll;
    protected ProjectBll projectBll;

    @Override
    public void init(Project project, ProjectDataSource projectDataSource, ProjectBll projectBll, NodeBll nodeBll, GraphNodeBll graphNodeBll) {
        this.project = project;
        this.projectDataSource = projectDataSource;
        this.graphNodeBll = graphNodeBll;
        this.nodeBll = nodeBll;
        this.projectBll = projectBll;
    }
}
