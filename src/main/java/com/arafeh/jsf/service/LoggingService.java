package com.arafeh.jsf.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingService {
    private static Logger logger = LoggerFactory.getLogger(LoggingService.class);


    public static void log(Object... msgs) {
        logger.info(buildMsg(msgs));
    }


    private static String buildMsg(Object[] msg) {
        StringBuilder builder = new StringBuilder();
        for (Object s : msg) {
            builder.append(String.valueOf(s)).append(" ");
        }
        return builder.toString();
    }
}
