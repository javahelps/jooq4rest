package com.javahelps.jooq4rest.repository;

import com.javahelps.jooq4rest.page.Page;
import jakarta.annotation.Nonnull;
import org.jooq.*;

import java.lang.Record;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An base class for JOOQ repositories providing common CRUD operations.
 *
 * @param <E> the type of the entity record
 * @param <P> the type of the projection record
 * @param <I> the type of the entity identifier
 */
public class JooqRepository<E extends Record, P extends Record, I> implements Repository<E, P, I> {

    private final DSLContext context;
    protected final Table<? extends UpdatableRecord<?>> table;
    private final Field<I> idField;
    protected final Class<E> entityClass;
    protected final Class<P> projectionClass;
    private final TransactionMode transactionMode;

    /**
     * Constructs an JooqRepository with the specified table, ID field, entity class, and projection class.
     *
     * @param context         the JOOQ DSL context
     * @param table           the JOOQ table
     * @param idField         the field representing the entity ID
     * @param entityClass     the class of the entity record
     * @param projectionClass the class of the projection record
     */
    protected JooqRepository(@Nonnull DSLContext context,
                             @Nonnull Table<? extends UpdatableRecord<?>> table,
                             @Nonnull Field<I> idField,
                             @Nonnull Class<E> entityClass,
                             @Nonnull Class<P> projectionClass) {
        this(context, table, idField, entityClass, projectionClass, TransactionMode.WRITE_ONLY);
    }

    /**
     * Constructs an JooqRepository with the specified table, ID field, entity class, and projection class.
     *
     * @param context         the JOOQ DSL context
     * @param table           the JOOQ table
     * @param idField         the field representing the entity ID
     * @param entityClass     the class of the entity record
     * @param projectionClass the class of the projection record
     * @param transactionMode the transaction mode
     */
    protected JooqRepository(@Nonnull DSLContext context,
                             @Nonnull Table<? extends UpdatableRecord<?>> table,
                             @Nonnull Field<I> idField,
                             @Nonnull Class<E> entityClass,
                             @Nonnull Class<P> projectionClass,
                             @Nonnull TransactionMode transactionMode) {
        this.context = Objects.requireNonNull(context);
        this.table = Objects.requireNonNull(table);
        this.idField = Objects.requireNonNull(idField);
        this.entityClass = Objects.requireNonNull(entityClass);
        this.projectionClass = Objects.requireNonNull(projectionClass);
        this.transactionMode = Objects.requireNonNull(transactionMode);
    }

    @Override
    public final boolean exists(@Nonnull I id) {
        if (this.transactionMode.isTransactionalRead()) {
            return this.context.transactionResult(configuration -> doExists(id));
        } else {
            return doExists(id);
        }
    }

    /**
     * Checks if an entity with the specified ID exists in the table.
     * <p>
     * Override this method to provide a custom implementation.
     *
     * @param id the ID of the entity to check for existence
     * @return true if the entity exists, false otherwise
     */
    protected boolean doExists(@Nonnull I id) {
        return this.context.fetchExists(this.table, this.idField.eq(id));
    }

    @Override
    public final long count() {
        if (this.transactionMode.isTransactionalRead()) {
            return context.transactionResult(configuration -> doCount(context));
        } else {
            return doCount(context);
        }
    }

    /**
     * Counts the number of records in the table.
     * <p>
     * Override this method to provide a custom implementation.
     *
     * @param context the DSL context
     * @return the count of records
     */
    protected long doCount(@Nonnull DSLContext context) {
        return context.fetchCount(this.table);
    }

    @Nonnull
    @Override
    public final Optional<E> findById(@Nonnull I id) {
        if (this.transactionMode.isTransactionalRead()) {
            return this.context.transactionResult(configuration -> doFindById(id));
        } else {
            return doFindById(id);
        }
    }

    /**
     * Finds an entity by its ID.
     * <p>
     * Override this method to provide a custom implementation.
     *
     * @param id the ID of the entity to find
     * @return an Optional containing the entity if found, or empty if not found
     */
    @Nonnull
    protected Optional<E> doFindById(@Nonnull I id) {
        return select(this.context)
                .from(this.table)
                .where(this.idField.eq(id))
                .fetchOptionalInto(this.entityClass);
    }

    /**
     * Finds all entities in the table.
     * <p>
     * Override this method to provide a custom implementation.
     *
     * @return a list of all entities
     */
    @Nonnull
    @Override
    public final List<E> findAll() {
        if (this.transactionMode.isTransactionalRead()) {
            return this.context.transactionResult(configuration -> doFindAll());
        } else {
            return doFindAll();
        }
    }

    @Nonnull
    protected List<E> doFindAll() {
        return select(this.context)
                .from(table)
                .fetchInto(this.entityClass);
    }

    @Nonnull
    @Override
    public final I insert(@Nonnull E entity) {
        if (this.transactionMode.isTransactionalWrite()) {
            return this.context.transactionResult(configuration -> doInsert(entity));
        } else {
            return doInsert(entity);
        }
    }

    /**
     * Inserts a new entity into the table.
     * <p>
     * Override this method to provide a custom implementation.
     *
     * @param entity the entity to insert
     * @return the ID of the inserted entity
     */
    @Nonnull
    protected I doInsert(@Nonnull E entity) {
        return Objects.requireNonNull(this.context.insertInto(this.table)
                .set(toRecord(this.context, entity))
                .returning(this.idField)
                .fetchOne(this.idField));
    }

    @Override
    public final int update(@Nonnull E entity) {
        if (this.transactionMode.isTransactionalWrite()) {
            return this.context.transactionResult(configuration -> doUpdate(entity));
        } else {
            return doUpdate(entity);
        }
    }

    /**
     * Updates an existing entity in the table.
     * <p>
     * Override this method to provide a custom implementation.
     *
     * @param entity the entity to update
     * @return the number of affected rows
     */
    protected int doUpdate(@Nonnull E entity) {
        UpdatableRecord<?> record = toRecord(this.context, entity);
        I id = record.get(this.idField);
        record.reset(this.idField);
        return this.context.update(this.table)
                .set(record)
                .where(this.idField.eq(id))
                .execute();
    }

    @Override
    public final int delete(@Nonnull I id) {
        if (this.transactionMode.isTransactionalWrite()) {
            return this.context.transactionResult(configuration -> doDelete(id));
        } else {
            return doDelete(id);
        }
    }

    /**
     * Deletes an entity by its ID from the table.
     * <p>
     * Override this method to provide a custom implementation.
     *
     * @param id the ID of the entity to delete
     * @return the number of affected rows
     */
    protected int doDelete(@Nonnull I id) {
        return this.context.deleteFrom(this.table)
                .where(this.idField.eq(id))
                .execute();
    }

    @Nonnull
    @Override
    public final Page<P> query(@Nonnull Condition condition, @Nonnull OrderField<?> order, int pageNumber, int pageSize) {
        if (this.transactionMode.isTransactionalRead()) {
            return this.context.transactionResult(configuration -> doQuery(condition, order, pageNumber, pageSize));
        } else {
            return doQuery(condition, order, pageNumber, pageSize);
        }
    }

    /**
     * Queries the table with the specified condition, order, page number, and page size.
     * <p>
     * Override this method to provide a custom implementation.
     *
     * @param condition  the condition to filter the query
     * @param order      the order field to sort the query
     * @param pageNumber the page number for pagination
     * @param pageSize   the page size for pagination
     * @return a Page containing the results of the query
     */
    @Nonnull
    protected Page<P> doQuery(@Nonnull Condition condition, @Nonnull OrderField<?> order, int pageNumber, int pageSize) {
        List<P> result = project(this.context)
                .from(this.table)
                .where(condition)
                .orderBy(order)
                .offset(pageNumber * pageSize)
                .limit(pageSize)
                .fetchInto(this.projectionClass);
        int count = this.context.fetchCount(table, condition);
        int totalPages = (int) Math.ceil((double) count / pageSize);
        return new Page<>(result, pageNumber, pageSize, totalPages, count);
    }

    /**
     * Override this method to customize the select query.
     *
     * @param context the DSL context
     * @return the select query
     */
    @Nonnull
    protected SelectSelectStep<?> select(@Nonnull DSLContext context) {
        return context.select();
    }

    /**
     * Override this method to customize the projection query.
     *
     * @param context the DSL context
     * @return the projection query
     */
    @Nonnull
    protected SelectSelectStep<?> project(@Nonnull DSLContext context) {
        return select(context);
    }

    /**
     * Override this method to customize the record creation.
     *
     * @param context the DSL context
     * @param entity  the entity to convert
     * @return the record
     */
    @Nonnull
    protected UpdatableRecord<?> toRecord(@Nonnull DSLContext context, @Nonnull E entity) {
        return context.newRecord(this.table, entity);
    }
}
