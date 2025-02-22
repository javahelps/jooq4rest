package com.javahelps.jooq4rest.condition;

import com.javahelps.jooq4rest.annotation.Equal;
import com.javahelps.jooq4rest.annotation.GreaterThanOrEqual;
import com.javahelps.jooq4rest.annotation.LikeIgnoreCase;

public record QueryParam(@Equal("name") String name,
                         @GreaterThanOrEqual("age") Integer age,
                         @LikeIgnoreCase("country") String country) {
}
