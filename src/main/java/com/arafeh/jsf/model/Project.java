package com.arafeh.jsf.model;

import com.arafeh.jsf.dal.datasource.ModelInterface;

import java.util.*;

import static com.arafeh.jsf.core.utils.Extensions.random;

public class Project implements ModelInterface {
    private long id;
    private String name;
    private ProjectDataSource dataSource;
    private ArrayList<String> dataAnalysisStrategies;
    private String navigatorStrategy;
    private ProjectState state;
    private String log;

    public Project(long id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    public Project() {
        this.dataAnalysisStrategies = new ArrayList<>();
        state = ProjectState.IDLE;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public void addLog(String log) {
        if (this.log == null) this.log = "";
        this.log += log + ";\n";
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(ProjectDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataAnalysisStrategies(ArrayList<String> dataAnalysisStrategies) {
        this.dataAnalysisStrategies = dataAnalysisStrategies;
    }

    public ArrayList<String> getDataAnalysisStrategies() {
        return dataAnalysisStrategies;
    }

    public void setNavigatorStrategy(String navigatorStrategy) {
        this.navigatorStrategy = navigatorStrategy;
    }

    public String getNavigatorStrategy() {
        return navigatorStrategy;
    }

    public ProjectState getState() {
        return state;
    }

    public boolean executable() {
        switch (state) {
            case FINISHED:
            case REMOVED:
            case STOPPED:
            case ERROR:
            case RESOURCES_EXHAUSTED:
                return false;
        }
        return true;
    }

    public void setState(ProjectState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Project) {
            return ((Project) obj).id == this.id;
        }
        return false;
    }
}
