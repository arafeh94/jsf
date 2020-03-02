package dynamicore.navigator;

import com.arafeh.jsf.dal.datasource.DataSourceInterface;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.input.DataInputBase;
import dynamicore.input.node.InputNode;

public abstract class NavigatorBase implements Navigator {
    protected Project project;
    protected ProjectDataSource dataSource;

    @Override
    public void init(Project project, Class<? extends DataInputBase> dataSourceInterface) {
        this.project = project;
        this.dataSource = project.getDataSource();
    }

    @Override
    public void successFetch() {

    }

    @Override
    public void failedFetch() {

    }

    @Override
    public void reset(InputNode node) {

    }
}
