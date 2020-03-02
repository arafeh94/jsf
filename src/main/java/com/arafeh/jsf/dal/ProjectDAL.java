package com.arafeh.jsf.dal;

import com.arafeh.jsf.dal.datasource.MongoDataAccessBase;
import com.arafeh.jsf.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import static com.mongodb.client.model.Filters.eq;

public class ProjectDAL extends MongoDataAccessBase<Project> {
    @Override
    public String getTableName() {
        return "projects";
    }

    @Override
    public Class<Project> getModelClass() {
        return Project.class;
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(ProjectDAL.class);
    }
}
