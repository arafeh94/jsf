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
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.NodeType;
import com.arafeh.jsf.model.Project;
import dynamicore.DynamicNetwork;
import dynamicore.analysing.Column;
import dynamicore.analysing.Row;
import dynamicore.analysing.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * IndexController class controller
 *
 * @author Giuseppe Gambino <joeg.ita@gmail.com>
 */
@Named
@ViewScoped
public class NodesView implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(NodesView.class);

    @Inject
    private
    NodeBll nodeService;

    private List<Node> nodes;

    @PostConstruct
    public void init() {
        nodes = this.nodeService.all();
    }

    /**
     * @return all nodes that are either of type account of follower
     */
    public List<Node> getAccountNodes() {
        return nodes.stream().filter(n -> NodeType.of(n.getType()) == NodeType.ACCOUNT || NodeType.of(n.getType()) == NodeType.FOLLOWER).collect(Collectors.toList());
    }

    /**
     * @return all nodes that treated as posts or comments (tweets and retweets)
     */
    public List<Node> getPostNodes() {
        return nodes.stream().filter(n -> NodeType.of(n.getType()) == NodeType.POST).collect(Collectors.toList());
    }
}
