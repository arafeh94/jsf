package com.arafeh.jsf.model;

import java.util.ArrayList;
import java.util.Collections;

import static com.arafeh.jsf.core.utils.Extensions.*;

public class ProjectDataSourceSettings {
    private String field;
    private String value;
    private String levels;

    public ProjectDataSourceSettings() {

    }

    public ProjectDataSourceSettings(String field, String value, String levels) {
        this.field = field;
        this.value = value;
        this.levels = levels;
    }


    public void setLevels(String levels) {
        this.levels = levels;
    }

    public String getLevels() {
        return levels;
    }

    @SuppressWarnings("RegExpSingleCharAlternation")
    public ArrayList<Integer> levelsAsInt() {
        ArrayList<Integer> levelsInt = new ArrayList<>();
        if (!isNullOrEmpty(levels)) {
            for (String level : levels.split(";|,")) {
                int val = intVal(level, 0);
                levelsInt.add(val);
            }
        }
        return levelsInt;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public int asInt() {
        return intVal(getValue(), -1);
    }

    public long asLong() {
        return longVal(getValue(), -1);
    }

    public long asLong(long or) {
        return longVal(getValue(), or);
    }

    public int asInt(int or) {
        return intVal(getValue(), or);
    }

    public String asString() {
        return getValue();
    }

    public ArrayList<String> asStringList() {
        ArrayList<String> list = new ArrayList<>();
        if (!isNullOrEmpty(asString())) {
            Collections.addAll(list, asString().split(";|,"));
        }
        return list;
    }

    public float asFloat() {
        return floatVal(getValue(), -1L);
    }

    public float asFloat(float def) {
        return floatVal(getValue(), def);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double asDouble() {
        return doubleVal(getValue(), -1.0d);
    }

    public double asDouble(double def) {
        return doubleVal(getValue(), def);
    }
}
