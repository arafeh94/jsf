package com.arafeh.jsf.dal.datasource;

public interface DataSourceInterface<T> {
    void connect();

    boolean isConnected();

    T client();
}
