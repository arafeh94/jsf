package com.arafeh.jsf.dal;

import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.model.Project;
import dynamicore.DynamicNetwork;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.arafeh.jsf.core.utils.Extensions.isNullOrEmpty;
import static javax.naming.InitialContext.doLookup;

public class TestTaskManagement {

    @Test
    public void text1() throws Exception {
        ProjectBll projectsBll = new ProjectBll();
        projectsBll.init();
        ArrayList<DynamicNetwork> networks = new ArrayList<>();
        List<Project> projects = projectsBll.all();
        for (Project project : projects) {
            switch (project.getState()) {
                case PENDING:
                case RUNNING:
                    if (networks.stream().noneMatch(n -> n.getProject().equals(project))) {
                        networks.add(DynamicNetwork.from(project));
                    }
                    break;
                case STOPPED:
                case REMOVED:
                case FINISHED:
                    networks.removeIf(n -> n.getProject().equals(project));
            }
        }
        while (!isNullOrEmpty(networks)) {
            DynamicNetwork dynamicNetwork = networks.get(0);
            networks.remove(0);
        }
    }
}
