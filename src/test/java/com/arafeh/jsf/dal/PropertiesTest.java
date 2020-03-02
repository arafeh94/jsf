package com.arafeh.jsf.dal;

import com.arafeh.jsf.config.AppProperties;
import org.junit.Test;

public class PropertiesTest {
    @Test
    public void test1(){
        AppProperties properties = AppProperties.getInstance();
        System.out.println(properties.getMongodbDatabase());
    }
}
