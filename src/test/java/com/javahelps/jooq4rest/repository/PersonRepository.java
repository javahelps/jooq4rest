package com.javahelps.jooq4rest.repository;

import com.javahelps.jooq4rest.repository.dto.Person;
import org.jooq.DSLContext;

import static com.javahelps.jooq4rest.repository.jooq.Person.PERSON;

public class PersonRepository extends UnifiedJooqRepository<Person, Long> {

    public PersonRepository(DSLContext context) {
        super(context, PERSON, PERSON.ID, Person.class);
    }

}
