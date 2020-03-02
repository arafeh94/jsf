package com.arafeh.jsf.dal;

import com.arafeh.jsf.dal.datasource.Neo4jDataAccessBase;
import com.arafeh.jsf.dal.datasource.Neo4jStringDataAccessBase;
import com.arafeh.jsf.model.GraphNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class GraphNodeDAL extends Neo4jStringDataAccessBase<GraphNode> implements Serializable {
    @Override
    public String getTableName() {
        return "GraphNode";
    }

    @Override
    public Class<GraphNode> getModelClass() {
        return GraphNode.class;
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(GraphNodeDAL.class);
    }

}
