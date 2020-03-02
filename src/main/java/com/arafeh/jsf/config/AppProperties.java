/*
 * The MIT License
 *
 * Copyright 2018 Giuseppe Gambino <joeg.ita@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.arafeh.jsf.config;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dynamicore.xc_input.twitter.TwitterAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileSystemView;

/**
 * AppProperties class
 *
 * @author Giuseppe Gambino <joeg.ita@gmail.com>
 */
public class AppProperties {

    private static AppProperties instance;

    public static AppProperties getInstance() {
        if (instance == null) {
            synchronized (AppProperties.class) {
                instance = new AppProperties();
                instance.loadExternalProperties();
            }
        }
        return instance;
    }

    public static final String NEO4J_URI = "neo4j.uri";
    public static final String NEO4J_USER = "neo4j.user";
    public static final String NEO4J_PASSWORD = "neo4j.password";

    public static final String MONGODB_URI = "mongodb.uri";
    public static final String MONGODB_DATABASE = "mongodb.database";
    public static final String MONGODB_USER = "mongodb.user";
    public static final String MONGODB_PASSWORD = "mongodb.password";
    public static final String MONGODB_HOST = "mongodb.host";
    public static final String MONGODB_PORT = "mongodb.port";

    public static final String LOCALTWITTER_HOST = "localtwitter.server";
    public static final String LOCALTWITTER_USER = "localtwitter.user";
    public static final String LOCALTWITTER_PASS = "localtwitter.password";
    public static final String LOCALTWITTER_DB = "localtwitter.database";
    public static final String MAPBOX_ACCESSTOCKEN = "mapbox.accessToken";

    public static final String LOGBACK_CONF = "log.configuration";

    public static final String OAUTHS = "twitter.oauths_file";

    public static final String DOC_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();

    public static final String APP_PATH = DOC_PATH + "/socins/";
    public static final String APP_CONF_PATH = APP_PATH + "conf/";
    public static final String APP_DATA_PATH = APP_PATH + "data/";

    public static final String APP_PROPERTIES_FILE = "app.properties";
    public static final String APP_CONF = "conf/";

    private static final Logger LOG = LoggerFactory.getLogger(AppProperties.class);
    private Properties properties = null;

    private void loadExternalProperties() {
        if (properties == null) {
            properties = loadProperties(getRootConfig());
            properties = properties == null ? new Properties() : properties;
        }
    }

    public String getNeo4jUser() {
        return properties.getProperty(NEO4J_USER);
    }

    public String getNeo4jPassword() {
        return properties.getProperty(NEO4J_PASSWORD);
    }

    public String getNeo4jUri() {
        return properties.getProperty(NEO4J_URI);
    }

    public String getMongodbUri() {
        return properties.getProperty(MONGODB_URI);
    }

    public String getMongodbHost() {
        return properties.getProperty(MONGODB_HOST);
    }

    public int getMongodbPort() {
        return Integer.parseInt(properties.getProperty(MONGODB_PORT));
    }

    public String getPackage() {
        return properties.getProperty("app.package");
    }

    public String getMongodbUser() {
        return properties.getProperty(MONGODB_USER);

    }
    public String getMapboxAccesstocken() {
        return properties.getProperty(MAPBOX_ACCESSTOCKEN);
    }

    public String getMongodbPassword() {
        return properties.getProperty(MONGODB_PASSWORD);
    }

    public String getMongodbDatabase() {
        return properties.getProperty(MONGODB_DATABASE);
    }
    public boolean isMongodbAuthRequired() {
        return Boolean.valueOf(properties.getProperty("mongodb.auth"));
    }

    public String getLocaltwitterHost() {
        return properties.getProperty(LOCALTWITTER_HOST);
    }

    public String getLocaltwitterUser() {
        return properties.getProperty(LOCALTWITTER_USER);
    }

    public String getLocaltwitterPass() {
        return properties.getProperty(LOCALTWITTER_PASS);
    }

    public String getLocaltwitterDb() {
        return properties.getProperty(LOCALTWITTER_DB);
    }

    private InputStream getRootConfig() {
        try {
            File conf = new File(APP_CONF_PATH + APP_PROPERTIES_FILE);
            if (conf.exists()) {
                return new FileInputStream(conf);
            } else {
                LOG.info("properties loaded from internal resource. \nIn case you need to customise your properties create this path {} and load your config into {}. Example of properties:\n{}", APP_CONF_PATH, APP_PROPERTIES_FILE, readConfig());
                return Resources.getResource(APP_CONF + APP_PROPERTIES_FILE).openStream();
            }
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage());
            return null;
        }
    }

    private Properties loadProperties(InputStream inputStream) {
        Properties appProps = new Properties();
        try {
            appProps.load(inputStream);
            inputStream.close();
        } catch (IOException ex) {
            LOG.error(ex.getLocalizedMessage());
        }
        return appProps;
    }

    private String readConfig() {
        InputStream stream = null;
        try {
            stream = Resources.getResource(APP_CONF + APP_PROPERTIES_FILE).openStream();
            StringBuilder builder = new StringBuilder();
            Scanner scanner = new Scanner(stream);
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            return builder.toString();
        } catch (IOException e) {
            return "no default configuration found in class directory";
        }
    }

    public ArrayList<TwitterAuth> twitterAuths() {
        try {
            Type listType = new TypeToken<ArrayList<TwitterAuth>>() {
            }.getType();
            InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(twitterAuthStream()));
            return new Gson().fromJson(reader, listType);
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }


    private InputStream twitterAuthStream() {
        try {
            loadExternalProperties();
            File conf = new File(APP_CONF_PATH + properties.getProperty(OAUTHS));
            if (conf.exists()) {
                return new FileInputStream(conf);
            } else {
                return Resources.getResource(APP_CONF + properties.getProperty(OAUTHS)).openStream();
            }
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage());
            return null;
        }
    }

}
