package com.arafeh.jsf.core.utils;

import com.arafeh.jsf.model.NodeType;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import dynamicore.input.Breadcrumb;
import dynamicore.input.node.InputLocation;
import dynamicore.input.node.InputNode;
import dynamicore.xc_analysis.NoneAnalysis;
import dynamicore.xc_input.random.RandomInputSource;
import dynamicore.xc_navigator.BFSNavigator;

import java.util.ArrayList;

import static com.arafeh.jsf.core.utils.Extensions.random;

public class StaticResources {
    static ArrayList<Integer> list = new ArrayList<>();

    public static Breadcrumb breadcrumb() {
        Breadcrumb breadcrumb = new Breadcrumb(1);
        breadcrumb.add(new InputLocation(1, 4));
        breadcrumb.add(new InputLocation(0, 0));
        breadcrumb.add(new InputLocation(1, 2));
        breadcrumb.add(new InputLocation(1, 1));
        breadcrumb.add(new InputLocation(1, 3));
        breadcrumb.add(new InputLocation(1, 0));
        breadcrumb.add(new InputLocation(2, 0));
        breadcrumb.add(new InputLocation(1, 2));
        breadcrumb.add(new InputLocation(2, 4));
        return breadcrumb;
    }

    public static InputNode graph(int depth) {
        InputNode parent = InputNode.create(0, 0, null, "root", NodeType.FOLLOWER, 0, 0);
        fill(parent, 1, depth);
        return parent;
    }

    private static void fill(InputNode parent, int depth, int max) {
        if (depth == max) return;
        for (int i = 0; i < 5; i++) {
            InputNode child = InputNode.create(unique(), 0, null, null, NodeType.FOLLOWER, depth, i);
            fill(child, depth + 1, max);
            parent.link(child);
        }
    }

    public static Project project(long id, String name) {
        if (id == 0) id = random().nextLong(0);
        if (name == null) name = TextGenerator.getInstance().getName();
        Project project = new Project(id, name);
        project.setDataAnalysisStrategies(new ArrayList<String>() {{
            add(NoneAnalysis.class.getName());
        }});
        Var<String> v = new Var<String>() {{
        }};
        project.setDataSource(new ProjectDataSource() {{
            setSource(RandomInputSource.class.getName());
            setStartingIds("1;2;3");
        }});
        project.setNavigatorStrategy(BFSNavigator.class.getName());
        return project;
    }

    public static Project project() {
        return project(0, null);
    }

    public static int unique() {
        int random = 1;
        while (list.contains(random)) {
            random = random().nextInt(1, 99999);
        }
        list.add(random);
        return random;
    }
}
