package com.arafeh.jsf.model;

import com.arafeh.jsf.dal.datasource.StringModelInterface;
import org.neo4j.ogm.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.arafeh.jsf.core.utils.Extensions.longVal;
import static org.neo4j.ogm.annotation.Relationship.*;


@NodeEntity
public class GraphNode implements StringModelInterface, Cloneable {
    @Id
    private String id;
    private boolean isDead;
    private String reason;
    private String json;
    private NodeType type;


    @Relationship(type = "linked", direction = INCOMING)
    private List<GraphNode> linked;

    public GraphNode() {

    }


    public GraphNode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<GraphNode> getLinked() {
        if (linked == null) return new ArrayList<>();
        return linked;
    }

    public void setLinked(List<GraphNode> linked) {
        this.linked = linked;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public boolean isDead() {
        return isDead;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    public String __field(int pos) {
        String[] split = getId().split(":");
        if (pos < split.length) return split[pos];
        return "";
    }

    public long __id() {
        return longVal(__field(0), -1);
    }

    public long __projectId() {
        return longVal(__field(1), -1);
    }

    public String __source() {
        return __field(2);
    }


}
