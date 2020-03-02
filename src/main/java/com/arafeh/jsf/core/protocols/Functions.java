package com.arafeh.jsf.core.protocols;

@SuppressWarnings("ALL")
public interface Functions<RETURN, PARAM> {
    RETURN run(PARAM... param);
}
