package com.arafeh.jsf.dal.datasource;


import com.arafeh.jsf.config.AppProperties;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import java.io.Serializable;
import java.util.Arrays;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Neo4jDataSource implements DataSourceInterface<SessionFactory> {
    public static Logger logger = LoggerFactory.getLogger(MongoDataSource.class);

    private SessionFactory client;
    private static Neo4jDataSource instance;

    public static Neo4jDataSource getInstance() {
        if (instance == null) {
            instance = new Neo4jDataSource();
            instance.connect();
        }
        return instance;
    }

    @Override
    public void connect() {
        if (client != null) return;
        try {

            AppProperties properties = AppProperties.getInstance();
            Configuration configuration = new Configuration.Builder()
                    .uri(properties.getNeo4jUri())
                    .credentials(properties.getNeo4jUser(), properties.getNeo4jPassword())
                    .build();
            client = new SessionFactory(configuration,
                    properties.getPackage() + ".model"
            );
        } catch (Exception e) {
            logger.error("error while connecting to neo4j", e);
        }

    }

    @Override
    public boolean isConnected() {
        return client != null;
    }

    @Override
    public SessionFactory client() {
        return client;
    }


}
