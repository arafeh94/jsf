package com.arafeh.jsf.service;

import com.arafeh.jsf.config.AppProperties;
import com.arafeh.jsf.core.utils.Extensions;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import java.util.ArrayList;

import static org.neo4j.driver.v1.Values.parameters;

@Singleton
@LocalBean
public class Neo4jExecutorService {
    public static Logger logger = LoggerFactory.getLogger(Neo4jExecutorService.class);
    private static Neo4jExecutorService instance;
    private final Driver driver;

    @PostConstruct
    public void init() {
        if (instance == null) {
            instance = new Neo4jExecutorService();
        }
    }

    public Neo4jExecutorService() {
        AppProperties properties = AppProperties.getInstance();
        String uri = properties.getNeo4jUri();
        String username = properties.getNeo4jUser();
        String password = properties.getNeo4jPassword();
        if (Extensions.isNullOrEmpty(uri, username, password)) {
            throw new RuntimeException("neo4j is not configured, please relate to app configuration and add the required properties: neo4j.uri neo4j.user neo4j.password");
        } else {
            driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "neo4j"));
        }
    }

    public StatementResult execute(String cypher, Value parameters) {
        try (Session session = driver.session()) {
            return session.writeTransaction(tx -> {
                return tx.run(cypher, parameters);
            });
        } catch (Exception e) {
            logger.error("error while executing neo4j statement", e);
            return null;
        }
    }


    public void execute(ArrayList<Statement> statements) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                for (Statement statement : statements) {
                    tx.run(statement);
                }
                return true;
            });
        } catch (Exception e) {
            logger.error("error while executing neo4j statement", e);
        }
    }

    public void execute(Statement... statements) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                for (Statement statement : statements) {
                    tx.run(statement);
                }
                return true;
            });
        } catch (Exception e) {
            logger.error("error while executing neo4j statement", e);
        }
    }

    public StatementResult execute(String cypher) {
        try (Session session = driver.session()) {
            return session.writeTransaction(tx -> {
                return tx.run(cypher);
            });
        } catch (Exception e) {
            logger.error("error while executing neo4j statement", e);
            return null;
        }
    }

}
