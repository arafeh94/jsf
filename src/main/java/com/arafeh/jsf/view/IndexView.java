/*
 * The MIT License
 *
 * Copyright 2018 Giuseppe Gambino <joeg.ita@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.arafeh.jsf.view;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectState;
import com.arafeh.jsf.service.TaskManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.arafeh.jsf.core.utils.Extensions.mapOf;
import static com.arafeh.jsf.core.utils.JsfExtension.redirect;
import static com.arafeh.jsf.core.utils.JsfExtension.refresh;
import static com.arafeh.jsf.core.utils.JsfExtension.throwMsg;

/**
 * IndexController class controller
 *
 * @author Giuseppe Gambino <joeg.ita@gmail.com>
 */
@Named
@ViewScoped
public class IndexView implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(IndexView.class);

    @Inject
    private
    ProjectBll projectService;

    @Inject
    private
    GraphNodeBll graphService;

    @Inject
    private
    TaskManagement taskManagement;

    private List<Project> projects;

    @PostConstruct
    public void init() {
        projects = projectService.allBut(ProjectState.DRAFT);
    }

    public void onLoad() {
        taskManagement.notified();
    }


    public List<Project> getProjects() {
        return projects;
    }

    public void delete(long id) {
        Optional<Project> project = projectService.get(id);
        LOG.error(project.toString());
        project.ifPresent(p -> {
            if (p.getState().equals(ProjectState.RUNNING)) {
                throwMsg("Delete Project", "Can't delete running project, you must stop it first");
            } else {
                boolean res = projectService.delete(p);
                if (res) {
                    if (p.getDataSource().getGraphId() != null) {
                        graphService.get(p.getDataSource().getGraphId()).ifPresent(graphNode -> graphService.delete(graphNode));
                    }
                    refresh();
                } else {
                    throwMsg("Delete Project", "Can't delete this project");
                }
            }
        });
    }

    public void stop(long id) {
        Optional<Project> project = projectService.get(id);
        LOG.error(project.toString());
        project.ifPresent(p -> {
            p.setState(ProjectState.STOPPED);
            projectService.set(p);
            refresh();
        });
    }

    public void run(long id) {
        Optional<Project> project = projectService.get(id);
        LOG.error(project.toString());
        project.ifPresent(p -> {
            p.setState(ProjectState.PENDING);
            projectService.set(p);
            refresh();
        });
    }

    public void update(long id) {
        redirect("crud.xhtml", mapOf("projectId", id));
    }
}
