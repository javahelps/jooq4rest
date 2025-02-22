package com.javahelps.jooq4rest.condition;

import com.javahelps.jooq4rest.annotation.Equal;
import com.javahelps.jooq4rest.annotation.GreaterThanOrEqual;
import com.javahelps.jooq4rest.annotation.In;
import com.javahelps.jooq4rest.annotation.LikeIgnoreCase;
import jakarta.annotation.Nonnull;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

/**
 * A utility class to extract JOOQ conditions from annotated fields of a given object.
 *
 * @param <R> the type of the JOOQ record
 * @param <T> the type of the object containing the annotated fields
 */
public class ConditionExtractor<R extends Record, T> {

    private static final Map<Class<? extends Annotation>, List<Class<?>>> SUPPORTED_TYPES = Map.of(
            Equal.class, List.of(String.class, Integer.class, Long.class, LocalDate.class),
            LikeIgnoreCase.class, List.of(String.class),
            In.class, List.of(Collection.class),
            GreaterThanOrEqual.class, List.of(String.class, Integer.class, Long.class, LocalDate.class)
    );
    private static final List<Class<? extends Annotation>> SUPPORTED_ANNOTATIONS = List.of(Equal.class, LikeIgnoreCase.class, In.class, GreaterThanOrEqual.class);

    private final Table<R> table;
    private final Class<T> clazz;
    private final Map<Class<? extends Annotation>, List<FieldAnnotationValue>> annotatedFields;

    /**
     * Constructs a ConditionExtractor for the given table and class.
     *
     * @param table the JOOQ table
     * @param clazz the class containing the annotated fields
     */
    public ConditionExtractor(@Nonnull Table<R> table, @Nonnull Class<T> clazz) {
        this.table = Objects.requireNonNull(table);
        this.clazz = Objects.requireNonNull(clazz);
        this.annotatedFields = extractAnnotatedFields(clazz);
    }

    /**
     * Extracts the annotated fields from the given class.
     *
     * @param clazz the class containing the annotated fields
     * @return a map of annotation types to lists of fields
     */
    private Map<Class<? extends Annotation>, List<FieldAnnotationValue>> extractAnnotatedFields(Class<T> clazz) {
        Map<Class<? extends Annotation>, List<FieldAnnotationValue>> annotatedFields = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            for (Class<? extends Annotation> annotation : SUPPORTED_ANNOTATIONS) {
                if (field.isAnnotationPresent(annotation)) {
                    String value = extractValue(field, annotation);
                    if (!Objects.requireNonNull(SUPPORTED_TYPES.get(annotation)).contains(field.getType())) {
                        throw new IllegalArgumentException(clazz.getCanonicalName() + "#" + field.getName() + " has an unsupported type of " + field.getType().getCanonicalName());
                    }
                    field.setAccessible(true); // Make the field accessible
                    annotatedFields.computeIfAbsent(annotation, k -> new ArrayList<>()).add(new FieldAnnotationValue(field, value));
                }
            }
        }
        return annotatedFields;
    }

    private static String extractValue(Field field, Class<? extends Annotation> annotation) {
        if (annotation == Equal.class) {
            return Objects.requireNonNull(field.getAnnotation(Equal.class)).value();
        } else if (annotation == GreaterThanOrEqual.class) {
            return Objects.requireNonNull(field.getAnnotation(GreaterThanOrEqual.class)).value();
        } else if (annotation == LikeIgnoreCase.class) {
            return Objects.requireNonNull(field.getAnnotation(LikeIgnoreCase.class)).value();
        } else if (annotation == In.class) {
            return Objects.requireNonNull(field.getAnnotation(In.class)).value();
        } else {
            throw new IllegalArgumentException("Unsupported annotation type: " + annotation.getCanonicalName());
        }
    }

    /**
     * Extracts the JOOQ condition from the annotated fields of the given object.
     *
     * @param object the object containing the annotated fields
     * @return the JOOQ condition
     */
    @Nonnull
    public Condition extract(@Nonnull T object) {
        Condition condition = DSL.trueCondition();
        for (Map.Entry<Class<? extends Annotation>, List<FieldAnnotationValue>> entry : this.annotatedFields.entrySet()) {
            for (FieldAnnotationValue fieldAnnotationValue : entry.getValue()) {
                Object fieldValue;
                try {
                    fieldValue = fieldAnnotationValue.field().get(object);
                    if (fieldValue == null) {
                        continue;
                    } else if (fieldValue instanceof Collection<?> collection && collection.isEmpty()) {
                        continue;
                    }
                } catch (IllegalAccessException e) {
                    continue;
                }
                if (entry.getKey() == Equal.class) {
                    switch (fieldValue) {
                        case String obj ->
                                condition = condition.and(Objects.requireNonNull(table.field(fieldAnnotationValue.annotationValue(), String.class)).eq(obj));
                        case Integer obj ->
                                condition = condition.and(Objects.requireNonNull(table.field(fieldAnnotationValue.annotationValue(), Integer.class)).eq(obj));
                        case Long obj ->
                                condition = condition.and(Objects.requireNonNull(table.field(fieldAnnotationValue.annotationValue(), Long.class)).eq(obj));
                        case LocalDate obj ->
                                condition = condition.and(Objects.requireNonNull(table.field(fieldAnnotationValue.annotationValue(), LocalDate.class)).eq(obj));
                        default ->
                                throw new IllegalArgumentException(clazz.getCanonicalName() + "#" + fieldAnnotationValue.field() + " has an unsupported type of " + fieldValue.getClass().getCanonicalName());
                    }
                } else if (entry.getKey() == GreaterThanOrEqual.class) {
                    switch (fieldValue) {
                        case String obj ->
                                condition = condition.and(Objects.requireNonNull(table.field(fieldAnnotationValue.annotationValue(), String.class)).ge(obj));
                        case Integer obj ->
                                condition = condition.and(Objects.requireNonNull(table.field(fieldAnnotationValue.annotationValue(), Integer.class)).ge(obj));
                        case Long obj ->
                                condition = condition.and(Objects.requireNonNull(table.field(fieldAnnotationValue.annotationValue(), Long.class)).ge(obj));
                        case LocalDate obj ->
                                condition = condition.and(Objects.requireNonNull(table.field(fieldAnnotationValue.annotationValue(), LocalDate.class)).ge(obj));
                        default ->
                                throw new IllegalArgumentException(clazz.getCanonicalName() + "#" + fieldAnnotationValue.field() + " has an unsupported type of " + fieldValue.getClass().getCanonicalName());
                    }
                } else if (entry.getKey() == LikeIgnoreCase.class) {
                    condition = condition.and(Objects.requireNonNull(table.field(fieldAnnotationValue.annotationValue())).likeIgnoreCase("%" + fieldValue + "%"));
                } else if (entry.getKey() == In.class) {
                    if (fieldValue instanceof Collection<?> collection) {
                        condition = condition.and(Objects.requireNonNull(table.field(fieldAnnotationValue.annotationValue())).in(collection));
                    }
                }
            }
        }
        return condition;
    }
}

