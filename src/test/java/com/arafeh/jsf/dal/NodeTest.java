package com.arafeh.jsf.dal;

import ch.qos.logback.classic.LoggerContext;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.core.utils.LogUtils;
import com.arafeh.jsf.core.utils.StaticResources;
import com.arafeh.jsf.core.utils.TextGenerator;
import com.arafeh.jsf.model.Node;
import dynamicore.input.node.InputNode;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.logging.LogManager;

import static org.junit.Assert.assertSame;

public class NodeTest {
    Logger logger = LoggerFactory.getLogger(NodeTest.class);

    @Test
    public void cycle() {
    }


    public int Id() {
        return StaticResources.unique();
    }

}
