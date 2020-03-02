package com.arafeh.jsf.core.utils;

import com.arafeh.jsf.core.protocols.Action;
import org.slf4j.Logger;

import java.util.*;

public class Html {
    public enum Color {
        RED("red"), GREEN("green"), BLUE("blue");

        final String color;

        Color(String color) {
            this.color = color;
        }
    }

    public static String color(String text, Color color) {
        return String.format("<font color='%s'>%s</font>", color.color, text);
    }

    public static String bold(String text) {
        return String.format("<b>%s</b>", text);
    }

    public static String clean(String html) {
        return html.replaceAll("<[^>]*>", "");
    }
}
