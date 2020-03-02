package com.arafeh.jsf.core.protocols;

public interface Function<RETURN, PARAM> {
    RETURN run(PARAM param);
}
