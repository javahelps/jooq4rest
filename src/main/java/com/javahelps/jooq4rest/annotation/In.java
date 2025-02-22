package com.javahelps.jooq4rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify an IN condition for a field.
 * The value should be the column name in the database.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * public class QueryParams {
 *     @In("role")
 *     private List<String> roles;
 * }
 * }
 * </pre>
 * <p>
 * The above example will generate a query like this if the value of the field is List.of("admin", "user"):
 * <pre>
 * {@code
 * role IN ('admin', 'user')
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface In {

    /**
     * The column name in the database.
     *
     * @return the column name in the database.
     */
    String value();
}
