package com.arafeh.jsf.dal.datasource;


import com.arafeh.jsf.config.AppProperties;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import java.util.Arrays;
import java.util.Collections;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDataSource implements DataSourceInterface<MongoClient> {
    public static Logger logger = LoggerFactory.getLogger(MongoDataSource.class);
    private MongoClient client;
    private static MongoDataSource instance;

    public static MongoDataSource getInstance() {
        if (instance == null) {
            instance = new MongoDataSource();
            instance.connect();
        }
        return instance;
    }

    @Override
    public void connect() {
        if (client != null) return;
        try {

            AppProperties properties = AppProperties.getInstance();
            MongoCredential credential = MongoCredential.createCredential(
                    properties.getMongodbUser(),
                    properties.getMongodbDatabase(),
                    properties.getMongodbPassword().toCharArray()
            );
            CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));

            MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder();
            if (properties.isMongodbAuthRequired()) settingsBuilder.credential(credential);
            MongoClientSettings settings = settingsBuilder
                    .applyToClusterSettings(builder -> {
                        builder.hosts(Arrays.asList(
                                new ServerAddress(properties.getMongodbHost(), properties.getMongodbPort())
                        ));
                    })
                    .codecRegistry(codecRegistry)
                    .build();

            client = MongoClients.create(settings);
        } catch (Exception e) {
            logger.error("error while connecting to mongo", e);
        }
    }

    @Override
    public boolean isConnected() {
        return client != null;
    }

    @Override
    public MongoClient client() {
        return client;
    }


}
