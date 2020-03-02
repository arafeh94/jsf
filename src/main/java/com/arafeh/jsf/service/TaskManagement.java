package com.arafeh.jsf.service;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.bll.RelationNodeBll;
import com.arafeh.jsf.model.Project;
import dynamicore.DynamicNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

import static com.arafeh.jsf.core.utils.Extensions.isNullOrEmpty;

/**
 * this class is a singleton for the whole service, it existence is for one and only
 * one purpose, execute pending, resume running DynamicNetwork projects.
 * currently whenever a network didn't execute we can stop its execution, but one
 * it start executing forget about it. need to fix anyone but not now
 */
@Singleton
@LocalBean
public class TaskManagement implements Serializable {
    private static Logger LOG = LoggerFactory.getLogger(TaskManagement.class);
    private ArrayList<DynamicNetwork> networks;

    private Future future;
    @Inject
    private ProjectBll projectsBll;
    @Inject
    private NodeBll nodeBll;
    @Inject
    private GraphNodeBll graphNodeBll;
    @Inject
    private RelationNodeBll relationNodeBll;

    @Resource
    private ManagedExecutorService executor;

    @PostConstruct
    public void init() {
        networks = new ArrayList<>();
    }

    /**
     * executing task one by one, you know currently we still in the development,
     * sometime we need to debug the network so it's better to run them one by one,
     * maybe later we change them to execute in parallel, not much change
     */
    @SuppressWarnings("ConstantConditions")
    public void execute() {
        if (future == null || future.isDone() && networks.size() > 0) {
            future = executor.submit(() -> {
                try {
                    while (!isNullOrEmpty(networks)) {
                        LOG.info("executing '{}'", networks.toString());
                        DynamicNetwork dynamicNetwork = networks.get(0);
                        LOG.info("executing '{}'", dynamicNetwork.getProject().getName());
                        if (dynamicNetwork != null) {
                            dynamicNetwork.inject(projectsBll, nodeBll, graphNodeBll, relationNodeBll).execute();
                        }
                        LOG.info("finished '{}'", dynamicNetwork.getProject().getName());
                        LOG.info("removing '{}'", dynamicNetwork.getProject().getName());
                        load();
                    }
                } catch (Exception e) {
                    LOG.error(e.getLocalizedMessage());
                }
            });
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void load() {
        LOG.info("loading networks");
        List<Project> projects = projectsBll.all();
        for (Project project : projects) {
            switch (project.getState()) {
                case PENDING:
                case RUNNING:
                    networks.removeIf(Objects::isNull);
                    if (networks.stream().noneMatch(n -> n.getProject().equals(project))) {
                        LOG.info("project pending execution: {}", project.getName());
                        networks.add(DynamicNetwork.from(project));
                    }
                    break;
                default:
                    networks.removeIf(n -> n == null || n.getProject().equals(project));
            }
        }
    }

    public void notified() {
        load();
        execute();
    }
}
