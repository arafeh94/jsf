package com.arafeh.jsf.core.utils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GsonTools {
    public static <T> Type listType() {
        return new TypeToken<ArrayList<T>>() {
        }.getType();
    }
}
