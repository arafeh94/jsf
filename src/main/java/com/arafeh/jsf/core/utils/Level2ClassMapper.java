package com.arafeh.jsf.core.utils;

import dynamicore.annotations.SettingsField;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("ALL")
public abstract class Level2ClassMapper {
    public abstract Class<? extends Level2ClassMapper> describe();

    private HashMap<String, Info> tree;

    public Collection<Info> content() {
        if (tree == null) return new ArrayList<>();
        return tree.values();
    }

    private void build() {
        if (tree != null) return;
        tree = new HashMap<>();
        for (Class<?> map : describe().getClasses()) {
            try {
                Object instance = map.newInstance();
                for (Field field : map.getDeclaredFields()) {
                    SettingsField fieldInfo = field.getAnnotation(SettingsField.class);
                    String fieldVal = field.get(instance).toString();
                    Info info = new Info() {{
                        setName(fieldInfo.name());
                        setCategory(fieldInfo.category().name());
                        setClassName(fieldInfo.assignedClass().getName());
                        setDescription(fieldInfo.description());
                    }};
                    tree.put(fieldVal, info);
                }
            } catch (Exception ignore) {
            }
        }
    }

    public Set<String> keySet() {
        build();
        return tree.keySet();
    }

    public Set<String> keySet(SettingsField.Category category) {
        build();
        LinkedHashSet<String> result = new LinkedHashSet<>();
        tree.forEach((key, val) -> {
            if (val.getCategory().equals(category.name())) result.add(key);
        });
        return result;
    }

    public String nameOf(String field) {
        build();
        if (tree.containsKey(field)) {
            return tree.get(field).getName();
        }
        return "";
    }

    public Optional<Info> infoOfName(String name) {
        build();
        if (tree.containsKey(name)) {
            return Optional.of(tree.get(name));
        }
        return Optional.empty();
    }

    public String descriptionOf(String field) {
        build();
        if (tree.containsKey(field)) {
            return tree.get(field).getDescription();
        }
        return "";
    }

    public String classOf(String field) {
        build();
        if (tree.containsKey(field)) {
            return tree.get(field).getClassName();
        }
        return "";
    }

    public static class Info {
        private String className = "";
        private String name = "";
        private String description = "";
        private String category = "";

        public Info() {
        }

        public String getClassName() {
            return className;
        }

        protected void setClassName(String className) {
            this.className = className;
        }

        public String getName() {
            return name;
        }

        protected void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        protected void setDescription(String description) {
            this.description = description;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCategory() {
            return category;
        }
    }

}
