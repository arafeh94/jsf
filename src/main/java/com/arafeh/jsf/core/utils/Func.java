package com.arafeh.jsf.core.utils;

import com.arafeh.jsf.core.protocols.Action;
import com.arafeh.jsf.core.protocols.Function;

public class Func<T> {
    private final Action<T> action;
    private long executionTime = 0;

    public Func(Action<T> action) {
        this.action = action;
    }

    public void runIf(boolean bool) {
        runIf(bool, null);
    }

    public void executionTime(boolean bool, String msg) {
        runIf(bool, null);
        if (bool) System.out.println(msg + executionTime);
    }

    public void runIf(boolean bool, T param) {
        this.executionTime = System.nanoTime();
        if (runnable() && bool) action.run(param);
        this.executionTime = System.nanoTime() - executionTime;
    }

    public void run() {
        runIf(true, null);
    }

    private boolean runnable() {
        return action != null;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
