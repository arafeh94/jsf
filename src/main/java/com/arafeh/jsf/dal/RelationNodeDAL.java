package com.arafeh.jsf.dal;

import com.arafeh.jsf.dal.datasource.MongoDataAccessBase;
import com.arafeh.jsf.model.RelationNode;
import com.mongodb.client.FindIterable;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

public class RelationNodeDAL extends MongoDataAccessBase<RelationNode> {
    @Override
    public String getTableName() {
        return "relationNodes";
    }

    @Override
    public Class<RelationNode> getModelClass() {
        return RelationNode.class;
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(RelationNodeDAL.class);
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof RelationNode) {
            RelationNode relationNode = (RelationNode) o;
            return collection().find(and(eq("left", relationNode.getLeft()), eq("right", relationNode.getRight()))).first() != null;
        }
        return false;
    }

    public FindIterable<RelationNode> findBatch(ArrayList<RelationNode> relationNodes) {
        ArrayList<Bson> filters = new ArrayList<>();
        for (RelationNode rn : relationNodes) {
            filters.add(or(and(eq("projectId", rn.getProjectId()), eq("type", rn.getType()), eq("left", rn.getLeft()), eq("right", rn.getRight()))));
        }
        return collection().find(or(filters));
    }


}
