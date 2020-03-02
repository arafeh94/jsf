package com.arafeh.jsf.dal;

import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.core.utils.StaticResources;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.DynamicNetwork;
import dynamicore.analysing.DataAnalysisInterface;
import dynamicore.input.DataInputInterface;
import dynamicore.navigator.Navigator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ProjectTest {
    static int ID = 1;
    static ProjectBll projectBll = new ProjectBll();

    static {
        projectBll.init();
    }

    @Test
    public void saveTests() {
        projectBll.set(project("arafeh"));
        System.out.println(projectBll.all());
    }


    @SuppressWarnings("Duplicates")
    public Project project(String name) {
        return StaticResources.project(123, name);
    }
}
