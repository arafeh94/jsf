package com.arafeh.jsf.dal;

import com.arafeh.jsf.dal.datasource.MongoDataAccessBase;
import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

public class NodeDAL extends MongoDataAccessBase<Node> {
    @Override
    public String getTableName() {
        return "nodes";
    }

    @Override
    public Class<Node> getModelClass() {
        return Node.class;
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(NodeDAL.class);
    }

}
