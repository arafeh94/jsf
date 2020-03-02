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

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.model.Project;
import dynamicore.input.node.GraphMapper;
import dynamicore.input.node.NodeJsonConverter;
import dynamicore.input.node.InputNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Optional;

/**
 * IndexController class controller
 *
 * @author Giuseppe Gambino <joeg.ita@gmail.com>
 */
@Named
@ViewScoped
public class ProjectGraphView implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectGraphView.class);

    @Inject
    private ProjectBll projectService;

    private long projectId;
    private Project project;

    @PostConstruct
    public void init() {

    }


    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getProjectId() {
        return projectId;
    }

    public Project getProject() {
        return this.project;
    }

    public void onLoad() {
        LOG.error(projectId + ":id");
        Optional<Project> result = projectService.get(projectId);
        LOG.error(result.isPresent() + ":present");
        result.ifPresent(project -> {
            this.project = project;
        });
    }

}
