package com.arafeh.jsf.dal;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.config.AppProperties;
import com.arafeh.jsf.core.utils.Func;
import com.arafeh.jsf.core.utils.Pair;
import com.arafeh.jsf.model.GraphNode;
import org.junit.Assert;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Driver;

import java.sql.*;
import java.util.*;

import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jConnectionTest {
    private static long ID = 1;
    static GraphNodeBll graphNodeBll;

    static {
        graphNodeBll = new GraphNodeBll();
        graphNodeBll.init();
    }

    @Test
    public void delete() {
        graphNodeBll.clear();
    }


    @Test
    public void neo4jTest() {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "neo4j"));
        try (Session session = driver.session()) {
            String greeting = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    StatementResult result = tx.run("CREATE (a:Greeting) " +
                                    "SET a.message = $message " +
                                    "RETURN a.message + ', from node ' + id(a)",
                            parameters("message", "hello samira"));
                    return result.single().get(0).asString();
                }
            });
            System.out.println(greeting);
        }
    }

    public class Neo4jExecutorService {

    }


}
