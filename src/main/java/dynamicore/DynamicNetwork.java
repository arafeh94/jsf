package dynamicore;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.bll.RelationNodeBll;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSourceSettings;
import com.arafeh.jsf.model.ProjectState;
import com.arafeh.jsf.service.LoggingService;
import com.arafeh.jsf.service.TaskManagement;
import dynamicore.analysing.DataAnalysisInterface;
import dynamicore.analysing.Table;
import dynamicore.input.DataInputInterface;
import dynamicore.input.middlewares.DataInputMiddleware;
import dynamicore.navigator.Navigator;
import dynamicore.xc_middlewares.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.arafeh.jsf.core.utils.Extensions.isNullOrEmpty;

/**
 * each project has it's own DynamicNetwork,
 * - DynamicNetwork must have access to the project to get
 * the required information and also for other modifications
 * - must have access to the nodes to update them, in case of adding,
 * updating or deleting
 * - <b>most important part of dynamic network</b>
 * 1) DataAnalysis: an array of implementation with the role of giving
 * an analysis of the graph. example of analysis maybe like PageRankAnalysis
 * 2) DataSampling: only one instance of data sampling can be used
 * data sampling is a strategy usual random with the role of reducing
 * the data
 * 3) DataInputInterface: the input, usual a social api, or a local graph like
 * data source. as input example we have twitter api, facebook api or
 * a locally created api that mimic one of the web api (faster if it was
 * accessed from offline)
 */
@SuppressWarnings("unchecked")
public class DynamicNetwork implements Serializable {

    protected ProjectBll projects;
    protected NodeBll nodes;
    protected GraphNodeBll graphNodes;
    protected RelationNodeBll relationNodes;

    protected Project project;
    protected List<DataAnalysisInterface> dataAnalysisInterfaces;
    protected Navigator navigator;
    protected List<DataInputInterface> inputInterfaces;
    protected ArrayList<DataInputMiddleware> middlewares;

    private DynamicNetwork() {
        dataAnalysisInterfaces = new ArrayList<>();
        inputInterfaces = new ArrayList<>();
        middlewares = new ArrayList<>();
        this.projects = new ProjectBll();
        this.nodes = new NodeBll();
        this.graphNodes = new GraphNodeBll();
        this.relationNodes = new RelationNodeBll();
    }

    /**
     * one may ask why this function is important, it easy, because in web server the
     * calling service must set the serveries gotten from the beans injector.
     * while in tests, just creating new instance is enough.
     *
     * @param projectBll
     * @param nodeBll
     * @param graphNodeBll
     */
    public DynamicNetwork inject(ProjectBll projectBll, NodeBll nodeBll, GraphNodeBll graphNodeBll, RelationNodeBll relationNodes) {
        this.graphNodes = graphNodeBll;
        this.nodes = nodeBll;
        this.projects = projectBll;
        this.relationNodes = relationNodes;
        return this;
    }

    /**
     * executing is simple as start the graph observation while following a
     * defined strategy, the nodes and graphNodes will be also passed to
     * the project.
     */
    public void execute() {
        LoggingService.log("project state: " + project.getState());
        if (!project.executable()) return;
        changeState();
        inputInterfaces.forEach(inputInterface -> {
                    initiateSubscriptions();
                    navigator.init(project, inputInterface.describe());
                    try {
                        inputInterface.init(project, nodes, graphNodes, projects, relationNodes, middlewares);
                        inputInterface.observe(navigator);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggingService.log("input source observation stopped because of exception", e.getLocalizedMessage());
                    }
                }
        );
    }

    @SuppressWarnings({"SingleStatementInBlock", "unchecked"})
    private void initiateSubscriptions() {
        clearRegistrations();
        for (ProjectDataSourceSettings settings : project.getDataSource().getSettings()) {
            try {
                String className = Static_Settings.instance().classOf(settings.getField());
                if (isNullOrEmpty(className) || className.equals(Null.class.getName())) continue;
                Class<? extends DataInputMiddleware> middleware = (Class<? extends DataInputMiddleware>) Class.forName(className);
                register(middleware.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void changeState() {
        project.setState(ProjectState.RUNNING);
        projects.set(project);
    }

    public ArrayList<Table> analyse() {
        ArrayList<Table> result = new ArrayList<>();
        dataAnalysisInterfaces.forEach(analysis -> {
            analysis.init(project, project.getDataSource(), projects, nodes, graphNodes);
            Table res = analysis.analyse();
            result.add(res);
        });
        return result;
    }

    public void register(DataInputMiddleware middleware) {
        this.middlewares.add(middleware);
    }

    private void clearRegistrations() {
        this.middlewares.clear();
    }

    public void registerAll(Collection<DataInputMiddleware> middlewares) {
        this.middlewares.addAll(middlewares);
    }

    public Project getProject() {
        return project;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Dynamic Network for project {")
                .append(project.getName()).append("}");
        builder.append("\n").append("Static_DataSources: ").append("\n");
        for (DataInputInterface inputInterface : inputInterfaces) {
            builder.append("\t").append(inputInterface.describe().getName())
                    .append("\n");
        }
        builder.append("Data Static_Analysis: ").append("\n");
        for (DataAnalysisInterface dai : dataAnalysisInterfaces) {
            builder.append("\t").append(dai.describe().getName())
                    .append("\n");
        }
        builder.append("Navigator: ").append(navigator.describe().getName())
                .append("\n");
        return builder.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ArrayList<Class<? extends DataAnalysisInterface>> analysisInterfaces;
        private ArrayList<Class<? extends DataInputInterface>> inputInterfaces;
        private Class<? extends Navigator> navigator;
        private Project project;

        public Builder() {
            analysisInterfaces = new ArrayList<>();
            inputInterfaces = new ArrayList<>();
        }

        public Builder addDataAnalysis(Class<? extends DataAnalysisInterface> analysisInterface) {
            this.analysisInterfaces.add(analysisInterface);
            return this;
        }

        public Builder addDataSource(Class<? extends DataInputInterface> inputInterface) {
            this.inputInterfaces.add(inputInterface);
            return this;
        }

        public Builder setNavigator(Class<? extends Navigator> navigator) {
            this.navigator = navigator;
            return this;
        }

        public Builder setProject(Project project) {
            this.project = project;
            return this;
        }


        /**
         * this function is called whenever we need to instantiate a Dynamic
         * Network related to a specific project in this function we create a
         * new instance and builder them to get a remote ejb instance instead of a
         * java class instance (make sure this code is running on a web server)
         * after the injection ended we instantiate each of component by passing
         * them the project as parameter, they can chose whatever configuration
         * they need from the project
         */
        public DynamicNetwork build() {
            try {
                DynamicNetwork dynamicNetwork = new DynamicNetwork();
                dynamicNetwork.project = this.project;
                dynamicNetwork.navigator = this.navigator.newInstance();
                for (Class<? extends DataInputInterface> inputInterface : this.inputInterfaces) {
                    DataInputInterface dataInputInterface = inputInterface.newInstance();
                    dynamicNetwork.inputInterfaces.add(dataInputInterface);
                }
                for (Class<? extends DataAnalysisInterface> analysisInterface : this.analysisInterfaces) {
                    DataAnalysisInterface dataAnalysisInterface = analysisInterface.newInstance();
                    dynamicNetwork.dataAnalysisInterfaces.add(dataAnalysisInterface);
                }
                return dynamicNetwork;
            } catch (Exception e) {
                e.printStackTrace();
                LoggingService.log(e.getLocalizedMessage());
                return null;
            }
        }
    }


    public static DynamicNetwork from(Project project) {
        try {
            DynamicNetwork.Builder builder = DynamicNetwork.builder();
            builder.setProject(project);
            builder.setNavigator((Class<? extends Navigator>) Class.forName(project.getNavigatorStrategy()));
            for (String analysis : project.getDataAnalysisStrategies()) {
                builder.addDataAnalysis((Class<? extends DataAnalysisInterface>) Class.forName(analysis));
            }
            builder.addDataSource((Class<? extends DataInputInterface>) Class.forName(project.getDataSource().getSource()));
            return builder.build();
        } catch (Exception e) {
            LoggingService.log(e.getLocalizedMessage());
            return null;
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof DynamicNetwork) {
            return ((DynamicNetwork) obj).project.equals(this.project);
        }
        return false;
    }
}
