package com.arafeh.jsf.config;

import com.arafeh.jsf.core.data.Preferences;

public class WebProperties {
    public static String TWITTER_SERVICE = "twitter_service";

    public static String getTwitterService() {
        return Preferences.getInstance().get(TWITTER_SERVICE, "TwitterServiceImpl");
    }

    public static void setTwitterService(String service) {
        Preferences.getInstance().editor().put(TWITTER_SERVICE, service).commit();
    }
}
