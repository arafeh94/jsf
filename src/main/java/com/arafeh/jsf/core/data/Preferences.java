package com.arafeh.jsf.core.data;

import com.arafeh.jsf.config.AppProperties;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Preferences {
    private static String defaultFileName = "pref.properties";
    private static Preferences instance;
    private static Logger LOG = LoggerFactory.getLogger(Preferences.class);
    private Gson gson;

    public static Preferences getInstance() {
        if (instance == null) {
            synchronized (Preferences.class) {
                instance = new Preferences();
            }
        }
        return instance;
    }

    private File propertiesFile;
    private Properties properties;

    private Preferences() {
        this.gson = new Gson();
        propertiesFile = createPropertiesFileIfNotExists();
        if (propertiesFile != null) {
            properties = new Properties();
            load();
        }
    }

    public Editor editor() {
        return new Editor();
    }

    public String getResourcesPath() {
        return propertiesFile.getAbsolutePath();
    }

    private void load() {
        try {
            FileInputStream fileInputStream = new FileInputStream(propertiesFile);
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (Exception e) {
            LOG.error("unable to read properties file, {}", e.getLocalizedMessage());
        }
    }

    private void save() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(propertiesFile);
            properties.store(fileOutputStream, null);
            fileOutputStream.close();
        } catch (Exception e) {
            LOG.error("unable to write to properties file, {}",
                    e.getLocalizedMessage());
        }
    }

    private File createPropertiesFileIfNotExists() {
        try {
            File file = new File(AppProperties.APP_CONF_PATH);
            File core = new File(AppProperties.APP_CONF_PATH + defaultFileName);
            if (!file.exists() || !core.exists()) {
                file.mkdirs();
                core.createNewFile();
            }
            return core;
        } catch (Exception e) {
            LOG.error("unable to create properties file, {}", e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * search and return key
     *
     * @param key string indicate the preference key you need to recover
     * @return return the key result as string or null if not exists
     */
    public String get(String key) {
        Object val = properties.get(key);
        String result = null;
        if (val != null) {
            result = String.valueOf(val);
        }
        return result;
    }

    public Set<Object> keys() {
        return new HashSet<>(properties.keySet());
    }

    public String get(String key, String def) {
        Object value = properties.get(key);
        if (value == null) return def;
        return String.valueOf(value);
    }

    public <T> T deserialize(String key, Class<T> className) {
        if (!properties.contains(key)) return null;
        return gson.fromJson(String.valueOf(properties.get(key)), className);
    }

    public int size() {
        return properties.size();
    }

    public class Editor {

        public Editor put(String key, String value) {
            properties.put(key, value);
            return this;
        }

        public Editor remove(String key) {
            properties.remove(key);
            return this;
        }

        public Editor serialise(String key, Object value) {
            properties.put(key, gson.toJson(value));
            return this;
        }

        public Editor clear() {
            properties.clear();
            return this;
        }

        public void commit() {
            save();
        }
    }

}
