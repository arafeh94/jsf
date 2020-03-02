package com.arafeh.jsf.core.utils;

import com.arafeh.jsf.core.protocols.Action;
import org.slf4j.Logger;

import java.lang.reflect.Array;
import java.util.*;

public class Extensions {
    public static long ID = 0;

    public static long atomicId() {
        return ++ID;
    }

    @SafeVarargs
    public static <T> ArrayList<T> listOf(T... t) {
        ArrayList<T> list = new ArrayList<>(t.length);
        Collections.addAll(list, t);
        return list;
    }

    public static ArrayList<Long> asList(long[] ts) {
        ArrayList<Long> list = new ArrayList<>(ts.length);
        for (long t : ts) {
            list.add(t);
        }
        return list;
    }

    public static String classSimpleName(String fullName) {
        try {
            return Class.forName(fullName).getSimpleName();
        } catch (ClassNotFoundException e) {
            return "";
        }
    }

    public static void loop(int times, Action<Void> action) {
        while (times > 0) {
            action.run(null);
            times--;
        }
    }

    @SafeVarargs
    public static <T> Set<T> setOf(T... t) {
        HashSet<T> set = new HashSet<>(t.length);
        Collections.addAll(set, t);
        return set;
    }

    public static boolean isNullOrEmpty(String... strings) {
        for (String string : strings) {
            if (string == null || string.isEmpty()) return true;
        }
        return false;
    }

    public static boolean isNullOrEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> T nullOr(T t, T or) {
        return t != null ? t : or;
    }

    public static void except(Action action, Logger logger) {
        try {
            action.run(true);
        } catch (Exception e) {
            action.run(false);
            if (logger == null) {
                e.printStackTrace();
            } else {
                logger.error(e.getLocalizedMessage());
            }
        }
    }

    public static int intVal(String s, int def) {
        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return def;
        }
    }

    public static float floatVal(String s, float def) {
        try {
            return Float.valueOf(s);
        } catch (Exception e) {
            return def;
        }
    }

    public static double doubleVal(String s, double def) {
        try {
            return Double.valueOf(s);
        } catch (Exception e) {
            return def;
        }
    }

    public static Long longVal(String s, long def) {
        try {
            return Long.valueOf(s);
        } catch (Exception e) {
            return def;
        }
    }

    public static float floatVal(String s) {
        return floatVal(s, 0L);
    }

    public static long longVal(String s) {
        return longVal(s, 0L);
    }

    public static int intVal(String s) {
        return intVal(s, 0);
    }

    public static void except(Action action) {
        except(action, null);
    }

    public static RandomExtension random() {
        return new RandomExtension();
    }

    public static RandomExtension random(int seed) {
        return new RandomExtension(seed);
    }

    public static boolean in(Collection collection, Object... objects) {
        for (Object object : objects) {
            if (!collection.contains(object)) {
                return false;
            }
        }
        return true;
    }

    public static HashMap<String, Object> mapOf(Object... items) {
        HashMap<String, Object> map = new HashMap<>();
        if (items.length % 2 != 0) return map;
        for (int i = 0; i < items.length - 1; i+=2) {
            map.put(items[i].toString(), items[i + 1]);
        }
        return map;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

}
