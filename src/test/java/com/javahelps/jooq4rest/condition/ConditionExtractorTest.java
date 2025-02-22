package com.javahelps.jooq4rest.condition;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("SqlNoDataSourceInspection")
class ConditionExtractorTest {

    @Test
    void testExtract() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:ConditionExtractorTest", "test", "");
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE person (name VARCHAR(255), age INT, country VARCHAR(255))");
            statement.execute("INSERT INTO person VALUES ('John', 25, 'USA')");
            statement.execute("INSERT INTO person VALUES ('Jane', 30, 'UK')");
            statement.execute("INSERT INTO person VALUES ('Alice', 35, 'USA')");
            statement.execute("INSERT INTO person VALUES ('Bob', 40, 'UK')");
            statement.execute("INSERT INTO person VALUES ('Charlie', 45, 'USA')");

            DSLContext context = DSL.using(connection, org.jooq.SQLDialect.H2);

            Table<Record3<String, Integer, String>> table = context.select(DSL.field("name", String.class),
                            DSL.field("age", Integer.class),
                            DSL.field("country", String.class))
                    .from(DSL.table("person"))
                    .asTable("table");

            QueryParam queryParam = new QueryParam("John", 25, "USA");
            ConditionExtractor<Record3<String, Integer, String>, QueryParam> extractor = new ConditionExtractor<>(table, QueryParam.class);

            Result<Record1<String>> result = context.select(DSL.field("name", String.class))
                    .from(table)
                    .where(extractor.extract(queryParam))
                    .fetch();

            assertEquals(1, result.size());
            assertEquals("John", result.getFirst().value1());
        }
    }
}