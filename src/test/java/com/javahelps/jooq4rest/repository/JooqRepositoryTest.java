package com.javahelps.jooq4rest.repository;

import com.javahelps.jooq4rest.page.Page;
import com.javahelps.jooq4rest.repository.dto.Person;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.OrderField;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JooqRepositoryTest {

    private static JooqRepository<Person, Person, Long> repository;

    @BeforeAll
    public static void setUp() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:JooqRepositoryTest;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;", "test", "");
        DSLContext context = DSL.using(connection, SQLDialect.H2);
        repository = new PersonRepository(context);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE person (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), country VARCHAR(255))");
            statement.execute("INSERT INTO person (name, country) VALUES ('John', 'USA')");
            statement.execute("INSERT INTO person (name, country) VALUES ('Jane', 'UK')");
        }
    }

    @Test
    @Order(1)
    void testInsert() {
        Person person = new Person(null, "Alice", "USA");
        Long id = repository.insert(person);
        assertEquals(3L, id);
    }

    @Test
    @Order(2)
    void testUpdate() {
        Person person = new Person(1, "John Updated", "USA");
        int affectedRows = repository.update(person);
        assertEquals(1, affectedRows);
    }

    @Test
    @Order(3)
    void testQuery() {
        Condition condition = DSL.trueCondition();
        OrderField<?> order = DSL.field("id").asc();
        Page<Person> page = repository.query(condition, order, 0, 10);
        assertEquals(3, page.totalElements());
        assertEquals(List.of(new Person(1, "John Updated", "USA"), new Person(2, "Jane", "UK"), new Person(3, "Alice", "USA")), page.content());
    }

    @Test
    @Order(4)
    void testDelete() {
        int affectedRows = repository.delete(1L);
        assertEquals(1, affectedRows);

        List<Person> people = repository.findAll();
        assertEquals(2, people.size());
    }

    @Test
    @Order(5)
    void testGet() {
        Optional<Person> person = repository.findById(2L);
        assertEquals(Optional.of(new Person(2, "Jane", "UK")), person);
    }

    @Test
    @Order(6)
    void testCount() {
        assertEquals(2, repository.count());
    }

    @Test
    @Order(7)
    void testFindAll() {
        List<Person> people = repository.findAll();
        assertEquals(2, people.size());
        assertEquals(List.of(new Person(2, "Jane", "UK"), new Person(3, "Alice", "USA")), people);
    }

    @Test
    @Order(8)
    void testExists() {
        assertTrue(repository.exists(2L));
        assertFalse(repository.exists(1L));
    }
}
