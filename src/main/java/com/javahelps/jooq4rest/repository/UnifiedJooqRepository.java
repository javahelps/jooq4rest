package com.javahelps.jooq4rest.repository;

import jakarta.annotation.Nonnull;
import org.jooq.*;

import java.lang.Record;

/**
 * A {@link JooqRepository} that uses the same entity class for both the entity and the DTO.
 *
 * @param <E> the type of the entity record
 * @param <I> the type of the ID field
 */
public class UnifiedJooqRepository<E extends Record, I> extends JooqRepository<E, E, I> {

    /**
     * Creates a new instance of {@link UnifiedJooqRepository}.
     *
     * @param context     the {@link DSLContext} to be used to interact with the database.
     * @param table       the {@link Table} to be used to interact with the database.
     * @param idField     the {@link Field} to be used to interact with the database.
     * @param entityClass the {@link Class} of the entity to be used to interact with the database.
     */
    protected UnifiedJooqRepository(@Nonnull DSLContext context, @Nonnull Table<? extends UpdatableRecord<?>> table, @Nonnull Field<I> idField, @Nonnull Class<E> entityClass) {
        super(context, table, idField, entityClass, entityClass);
    }

    /**
     * Creates a new instance of {@link UnifiedJooqRepository}.
     *
     * @param context         the {@link DSLContext} to be used to interact with the database.
     * @param table           the {@link Table} to be used to interact with the database.
     * @param idField         the {@link Field} to be used to interact with the database.
     * @param entityClass     the {@link Class} of the entity to be used to interact with the database.
     * @param transactionMode the {@link TransactionMode} to be used to interact with the database.
     */
    protected UnifiedJooqRepository(@Nonnull DSLContext context, @Nonnull Table<? extends UpdatableRecord<?>> table, @Nonnull Field<I> idField, @Nonnull Class<E> entityClass, @Nonnull TransactionMode transactionMode) {
        super(context, table, idField, entityClass, entityClass, transactionMode);
    }

    @Nonnull
    @Override
    protected final SelectSelectStep<?> project(@Nonnull DSLContext context) {
        // No projection is needed for a uni-repository.
        return super.project(context);
    }
}
