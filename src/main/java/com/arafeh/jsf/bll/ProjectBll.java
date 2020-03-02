package com.arafeh.jsf.bll;

import com.arafeh.jsf.dal.ProjectDAL;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectState;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.arafeh.jsf.core.utils.Extensions.*;


@Singleton
@LocalBean
public class ProjectBll implements Serializable {
    private static ProjectDAL projects;

    public ProjectBll() {

    }

    @PostConstruct
    public void init() {
        if (projects == null) {
            projects = new ProjectDAL();
        }
    }

    public List<Project> all() {
        return projects.all();
    }

    public List<Project> all(ProjectState... state) {
        return all().stream().filter(p -> in(listOf(state), p.getState()))
                .collect(Collectors.toList());
    }

    public List<Project> allBut(ProjectState... state) {
        return all().stream().filter(p -> !in(listOf(state), p.getState()))
                .collect(Collectors.toList());
    }

    public Optional<Project> get(long id) {
        return projects.find(id);
    }

    public Optional<Project> get(String name) {
        return projects.find(mapOf("name", name)).stream().findFirst();
    }

    public boolean set(Project project) {
        if (projects.contains(project)) {
            projects.set(project.getId(), project);
            return true;
        } else {
            return projects.add(project);
        }
    }

    public long generateId() {
        long id;
        do {
            id = random().nextLong(0);
        } while (projects.find(id).isPresent());
        return id;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean delete(Object object) {
        return projects.remove(object);
    }

    public boolean executable(Project project) {
        Optional<Project> p = this.get(project.getId());
        return p.map(Project::executable).orElse(false);
    }

    public void clear() {
        projects.clear();
    }
}
