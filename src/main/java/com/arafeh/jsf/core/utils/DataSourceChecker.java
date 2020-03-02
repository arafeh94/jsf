package com.arafeh.jsf.core.utils;

import com.arafeh.jsf.config.AppProperties;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.Arrays;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DataSourceChecker {
    public static boolean checkMongoDB() {
        return false;
    }

    public static boolean checkNeo4jDB() {
        return false;
    }
}
