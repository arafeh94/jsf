package com.arafeh.jsf.dal.datasource;

import com.arafeh.jsf.core.protocols.Function;

import java.util.*;

public interface DataAccessInterface<T, CLIENT, ID> extends CustomList<T, ID> {
    public List<T> asList();

    public List<T> query(Function<List<T>, CLIENT> executor);

    public Optional<T> find(ID id);

    public List<T> find(Map<String, Object> query);

    public Optional<T> findOne(Map<String, Object> query);

    public List<T> findAll(ArrayList<ID> ids);

    public List<T> all();

}
