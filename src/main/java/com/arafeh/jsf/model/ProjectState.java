package com.arafeh.jsf.model;

import com.arafeh.jsf.dal.datasource.ModelInterface;

import java.util.ArrayList;

public enum ProjectState {
    IDLE, PENDING, RUNNING, STOPPED, REMOVED, FINISHED, DRAFT, RESOURCES_EXHAUSTED, ERROR;
}
