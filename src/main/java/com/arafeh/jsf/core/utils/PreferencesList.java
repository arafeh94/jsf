package com.arafeh.jsf.core.utils;

import com.arafeh.jsf.core.data.Preferences;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;

public class PreferencesList<E> extends LinkedList<E> {
    private String identifier;

    public PreferencesList(String identifier) {
        this.identifier = identifier;
    }

    public void save() {
        Preferences.getInstance().editor().serialise(identifier, this).commit();
    }

    public static <E> PreferencesList<E> load(String identifier) {
        String string = Preferences.getInstance().get(identifier);
        PreferencesList<E> list = new PreferencesList<>(identifier);
        if (string.equals("null")) {
            return list;
        }
        list.addAll(new Gson().fromJson(string, ArrayList.class));
        return list;
    }
}
