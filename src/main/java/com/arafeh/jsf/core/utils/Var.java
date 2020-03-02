package com.arafeh.jsf.core.utils;

public class Var<T> {
    private T object;

    public Var(T object) {
        this.object = object;
    }

    public Var() {
        this.object = null;
    }

    public T get() {
        return object;
    }

    public void set(T object) {
        this.object = object;
    }
}
