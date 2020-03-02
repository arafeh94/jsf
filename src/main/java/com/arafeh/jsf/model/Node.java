package com.arafeh.jsf.model;

import com.arafeh.jsf.dal.datasource.ModelInterface;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

public class Node implements ModelInterface {
    private long id;
    private String type;
    private String source;
    private Date createdAt;
    private Date expireAt;
    private HashMap<String, Object> store;

    public Node(NodeType type, String source) {
        this.type = type.name();
        this.source = source;
    }

    public Node() {

    }

    public void setType(String type) {
        this.type = type;
    }


    public String getType() {
        return type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HashMap<String, Object> getStore() {
        if (store == null) return new HashMap<>();
        return store;
    }

    public void setStore(HashMap<String, Object> store) {
        this.store = store;
    }

    public void set(String key, Object value) {
        if (this.store == null) this.store = new HashMap<>();
        this.store.put(key, value);
    }

    public Optional<Object> get(String key) {
        if (this.store == null) return Optional.empty();
        return Optional.ofNullable(this.store.get(key));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T def) {
        return (T) get(key).orElse(def);
    }

    public void store(String key, Object val) {
        set(key, val);
    }

    @Override
    public String toString() {
        return type + ": " + (store == null ? "empty" : store.toString());
    }
}
