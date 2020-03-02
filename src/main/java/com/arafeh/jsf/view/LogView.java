package com.arafeh.jsf.view;

import ch.qos.logback.classic.LoggerContext;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.model.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.FileReader;
import java.io.Serializable;
import java.util.List;
import java.util.logging.LogManager;

@Named
@ViewScoped
public class LogView implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(NodesView.class);



    private String log;

    @PostConstruct
    public void init() {
        LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
    }

}
