package com.arafeh.jsf.core.utils;

public class Convertible {
    private Object original;

    public Convertible(Object original) {
        this.original = original;
    }

    public String asString() {
        return this.original.toString();
    }

    public int asInt() {
        try {
            return Integer.valueOf(this.original.toString());
        } catch (Exception e) {
            return -1;
        }
    }

    public long asLong() {
        try {
            return Long.valueOf(this.original.toString());
        } catch (Exception e) {
            return -1L;
        }
    }

    public double asDouble() {
        try {
            return Double.valueOf(this.original.toString());
        } catch (Exception e) {
            return -1D;
        }
    }

    public boolean asBool() {
        try {
            return Boolean.getBoolean(this.original.toString());
        } catch (Exception e) {
            return false;
        }
    }
}
