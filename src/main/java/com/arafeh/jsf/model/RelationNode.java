package com.arafeh.jsf.model;

import com.arafeh.jsf.dal.datasource.ModelInterface;

public class RelationNode implements ModelInterface {
    private long id;
    private long projectId;
    private long left;
    private long right;
    private String type;
    private boolean isDead;
    protected String customType;
    protected String customRelation;
    protected String text;


    public RelationNode() {
    }

    public String getText() {
        if (text == null) return "G";
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCustomType() {
        if (this.customType == null) return "GraphNode";
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public String getCustomRelation() {
        if (this.customRelation == null) return "linked";
        return customRelation;
    }

    public void setCustomRelation(String customRelation) {
        this.customRelation = customRelation;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getLeft() {
        return left;
    }

    public void setLeft(long left) {
        this.left = left;
    }

    public long getRight() {
        return right;
    }

    public void setRight(long right) {
        this.right = right;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof RelationNode) {
            RelationNode rn = (RelationNode) obj;
            return this.left == rn.left && this.right == rn.right && this.projectId == rn.projectId && this.type.equals(rn.type);
        }
        return false;
    }

    @Override
    public String toString() {
        return "left: " + left + " - right: " + right;
    }
}
