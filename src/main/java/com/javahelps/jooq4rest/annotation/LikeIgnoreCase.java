package com.javahelps.jooq4rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify a case-insensitive LIKE condition for a field.
 * The value should be the column name in the database. Can be used on fields of type {@link String}.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * public class QueryParams {
 *     @LikeIgnoreCase("name")
 *     private String name;
 * }
 * }
 * </pre>
 * <p>
 * The above example will generate a query like this if the value of the field is "john":
 * <pre>
 * {@code
 * name ILIKE '%john%'
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface LikeIgnoreCase {

    /**
     * The column name in the database.
     *
     * @return the column name in the database.
     */
    String value();
}
