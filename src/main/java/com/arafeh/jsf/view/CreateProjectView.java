package com.arafeh.jsf.view;

import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.core.utils.Html;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import com.arafeh.jsf.model.ProjectDataSourceSettings;
import com.arafeh.jsf.model.ProjectState;
import dynamicore.Static_Analysis;
import dynamicore.Static_DataSources;
import dynamicore.Static_Navigators;
import dynamicore.Static_Settings;
import dynamicore.annotations.SettingsField;
import dynamicore.annotations.SettingsField.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

import static com.arafeh.jsf.core.utils.Extensions.in;
import static com.arafeh.jsf.core.utils.Extensions.isNullOrEmpty;
import static com.arafeh.jsf.core.utils.Extensions.mapOf;
import static com.arafeh.jsf.core.utils.JsfExtension.redirect;
import static com.arafeh.jsf.core.utils.JsfExtension.throwMsg;

/**
 * IndexController class controller
 *
 * @author arafeh <arafeh198@gmail.com>
 */
@Named
@ViewScoped
public class CreateProjectView implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(CreateProjectView.class);


    @Inject
    private
    ProjectBll projectService;

    private Project project;
    private long projectId;
    private String name;
    private String navigatorStrategy;
    private ProjectDataSource projectDataSource;
    private ArrayList<String> dataAnalysisStrategies;
    private ArrayList<ProjectDataSourceSettings> inputSettings = new ArrayList<>();
    private ArrayList<ProjectDataSourceSettings> analysisSettings = new ArrayList<>();
    private ArrayList<ProjectDataSourceSettings> navigatorSettings = new ArrayList<>();
    private ArrayList<ProjectDataSourceSettings> filters = new ArrayList<>();


    private Project draft;
    private List<Project> drafts;

    @PostConstruct
    public void init() {
        project = new Project();
        projectDataSource = new ProjectDataSource();
        project.setId(projectService.generateId());
        dataAnalysisStrategies = new ArrayList<>();
        drafts = projectService.all(ProjectState.DRAFT);
        setDataSource(null);
    }

    public void onLoad() {
        projectService.get(projectId).ifPresent(p -> {
            loadProject(p, false);
        });
    }

    private void loadProject(Project project, boolean asDraft) {
        Static_Settings instance = Static_Settings.instance();
        if (!asDraft) {
            this.setProjectId(project.getId());
            this.setName(project.getName());
        }
        this.setNavigatorStrategy(project.getNavigatorStrategy());
        this.setDataSource(project.getDataSource().getSource());
        this.setDataAnalysisStrategies(project.getDataAnalysisStrategies());
        this.projectDataSource = project.getDataSource();
        this.filters.clear();
        this.analysisSettings.clear();
        this.inputSettings.clear();
        this.navigatorSettings.clear();
        for (ProjectDataSourceSettings settings : project.getDataSource().getSettings()) {
            instance.infoOfName(settings.getField()).ifPresent(info -> {
                if (info.getCategory().equals(Category.FILTER.name())) {
                    filters.add(settings);
                } else if (info.getCategory().equals(Category.NAVIGATION.name())) {
                    navigatorSettings.add(settings);
                } else if (info.getCategory().equals(Category.ANALYSIS.name())) {
                    analysisSettings.add(settings);
                } else if (info.getCategory().equals(Category.INPUT.name())) {
                    inputSettings.add(settings);
                }
            });
        }
        cleanSettings();
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public Project getDraft() {
        return draft;
    }

    public void setDraft(Project draft) {
        this.draft = draft;
        if (draft != null) loadProject(draft, true);
    }

    public List<Project> getDrafts() {
        return drafts;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNavigatorStrategy() {
        return navigatorStrategy;
    }

    public void setNavigatorStrategy(String navigatorStrategy) {
        this.navigatorStrategy = navigatorStrategy;
    }

    public ProjectDataSource getDataSource() {
        return this.projectDataSource;
    }

    public void setDataSource(String dataSource) {
        if (dataSource != null) projectDataSource.setSource(dataSource);
        addFilter();
        addInputSettings();
        addNavigatorSettings();
        addAnalysisSettings();
    }


    public void addFilter() {
        cleanedSettings(filters).add(new ProjectDataSourceSettings());
    }

    public void addNavigatorSettings() {
        cleanedSettings(navigatorSettings).add(new ProjectDataSourceSettings());
    }

    public void addAnalysisSettings() {
        cleanedSettings(analysisSettings).add(new ProjectDataSourceSettings());
    }

    public void addInputSettings() {
        cleanedSettings(inputSettings).add(new ProjectDataSourceSettings());
    }

    public ArrayList<ProjectDataSourceSettings> getFilters() {
        return filters;
    }

    public ArrayList<ProjectDataSourceSettings> getNavigatorSettings() {
        return navigatorSettings;
    }

    public ArrayList<ProjectDataSourceSettings> getInputSettings() {
        return inputSettings;
    }

    public ArrayList<ProjectDataSourceSettings> getAnalysisSettings() {
        return analysisSettings;
    }

    public void setDataAnalysisStrategies(ArrayList<String> dataAnalysisStrategies) {
        this.dataAnalysisStrategies = dataAnalysisStrategies;
    }

    public ArrayList<String> getDataAnalysisStrategies() {
        return dataAnalysisStrategies;
    }

    private Optional<Project> validate() {
        Optional passed = Optional.of(new Object());
        if (isNullOrEmpty(name)) {
            throwMsg("Project Name", "can't be empty");
            passed = Optional.empty();
        }
        if (isNullOrEmpty(projectDataSource.getSource())) {
            throwMsg("Data Source", "Should at least choose one data source");
            passed = Optional.empty();
        }
        if (isNullOrEmpty(navigatorStrategy)) {
            throwMsg("Navigation Strategy", "can't be empty");
            passed = Optional.empty();
        }
        if (isNullOrEmpty(dataAnalysisStrategies)) {
            throwMsg("Data Analysis", "Should at least chose one analysis strategy");
            passed = Optional.empty();
        }
        if (isNullOrEmpty(projectDataSource.getStartingIds())) {
            throwMsg("Seed Nodes", "can't be empty");
            passed = Optional.empty();
        }
        if (passed.isPresent()) {
            projectDataSource.getSettings().clear();
            projectDataSource.getSettings().addAll(cleanedSettings(analysisSettings));
            projectDataSource.getSettings().addAll(cleanedSettings(inputSettings));
            projectDataSource.getSettings().addAll(cleanedSettings(navigatorSettings));
            projectDataSource.getSettings().addAll(cleanedSettings(filters));
            project.setName(name);
            project.setDataSource(projectDataSource);
            project.setDataAnalysisStrategies(dataAnalysisStrategies);
            project.setNavigatorStrategy(navigatorStrategy);
            return Optional.of(project);
        }
        return Optional.empty();
    }

    private ArrayList<ProjectDataSourceSettings> cleanedSettings(ArrayList<ProjectDataSourceSettings> settings) {
        settings.removeIf(st -> isNullOrEmpty(st.getField(), st.getValue()));
        return settings;
    }

    private void cleanSettings() {
        addInputSettings();
        addAnalysisSettings();
        addFilter();
        addNavigatorSettings();
    }

    public void submit() {
        validate().ifPresent(p -> {
            //update draft
            if (projectId != 0) {
                p.setId(projectId);
            }
            projectService.set(p);
            redirect("index.xhtml");
        });
    }

    public void submitAndRun() {
        validate().ifPresent(p -> {
            if (projectId != 0) {
                p.setId(projectId);
            }
            p.setState(ProjectState.PENDING);
            projectService.set(p);
            redirect("index.xhtml");
        });
    }

    public void saveAsDraft() {
        validate().ifPresent(p -> {
            Optional<Project> op = projectService.get(p.getName());
            if (op.isPresent() && op.get().getState() == ProjectState.DRAFT) {
                throwMsg("Exists", "Draft Already Exists");
            } else {
                p.setState(ProjectState.DRAFT);
                projectService.set(p);
                redirect("crud.xhtml", mapOf("created", "true"));
            }
        });
    }


    public HashMap<String, String> getAvailableNavigators() {
        Static_Navigators staticNavigators = new Static_Navigators();
        HashMap<String, String> map = new HashMap<>();
        staticNavigators.keySet().forEach(key -> map.put(htmlClean(staticNavigators.nameOf(key)), staticNavigators.classOf(key)));
        return map;
    }

    private String htmlClean(String name) {
        return Html.clean(name);
    }

    public HashMap<String, String> getAvailableDataAnalysisStrategy() {
        Static_Analysis analysis = new Static_Analysis();
        HashMap<String, String> map = new HashMap<>();
        analysis.keySet().forEach(key -> map.put(Html.clean(analysis.nameOf(key)), analysis.classOf(key)));
        return map;
    }

    public HashMap<String, String> getAvailableDataSources() {
        Static_DataSources staticDataSources = new Static_DataSources();
        HashMap<String, String> map = new HashMap<>();
        staticDataSources.keySet().forEach(key -> map.put(Html.clean(staticDataSources.nameOf(key)), staticDataSources.classOf(key)));
        return map;
    }

    public HashMap<String, String> getAvailableFilters() {
        Static_Settings instance = Static_Settings.instance();
        HashMap<String, String> map = new HashMap<>();
        instance.keySet(Category.FILTER).forEach(key -> map.put(Html.clean(instance.nameOf(key)), key));
        return map;
    }

    public HashMap<String, String> getAvailableNavigatorSettings() {
        Static_Settings instance = Static_Settings.instance();
        HashMap<String, String> map = new HashMap<>();
        instance.keySet(Category.NAVIGATION).forEach(key -> map.put(Html.clean(instance.nameOf(key)), key));
        return map;
    }

    public HashMap<String, String> getAvailableInputSettings() {
        Static_Settings instance = Static_Settings.instance();
        HashMap<String, String> map = new HashMap<>();
        instance.keySet(Category.INPUT).forEach(key -> map.put(Html.clean(instance.nameOf(key)), key));
        return map;
    }

    public HashMap<String, String> getAvailableAnalysisSettings() {
        Static_Settings instance = Static_Settings.instance();
        HashMap<String, String> map = new HashMap<>();
        instance.keySet(Category.ANALYSIS).forEach(key -> map.put(Html.clean(instance.nameOf(key)), key));
        return map;
    }

    public String getFiltersDescription() {
        Static_Settings instance = Static_Settings.instance();
        StringBuilder builder = new StringBuilder();
        instance.keySet(Category.FILTER).forEach(key -> builder.append("<font color='blue'>").append("<b>").append(instance.nameOf(key)).append("</b>").append("</font>").append(":").append(instance.descriptionOf(key)).append("<br>"));
        return builder.toString();
    }
}
