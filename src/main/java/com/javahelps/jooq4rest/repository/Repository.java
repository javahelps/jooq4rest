package com.javahelps.jooq4rest.repository;

import com.javahelps.jooq4rest.page.Page;
import jakarta.annotation.Nonnull;
import org.jooq.Condition;
import org.jooq.OrderField;

import java.util.List;
import java.util.Optional;

/**
 * A repository interface for performing CRUD operations and queries using JOOQ.
 *
 * @param <E> the type of the entity record
 * @param <P> the type of the projection record
 * @param <I> the type of the entity identifier
 */
public interface Repository<E extends Record, P extends Record, I> {

    /**
     * Checks if an entity with the given ID exists.
     *
     * @param id the entity ID
     * @return true if the entity exists, false otherwise
     */
    boolean exists(@Nonnull I id);

    /**
     * Counts the total number of entities.
     *
     * @return the total number of entities
     */
    long count();

    /**
     * Finds an entity by its ID.
     *
     * @param id the entity ID
     * @return an optional containing the entity if found, or empty if not found
     */
    @Nonnull
    Optional<E> findById(@Nonnull I id);

    /**
     * Finds all entities.
     *
     * @return a list of all entities
     */
    @Nonnull
    List<E> findAll();

    /**
     * Inserts a new entity.
     *
     * @param entity the entity to insert
     * @return the ID of the inserted entity
     */
    @Nonnull
    I insert(@Nonnull E entity);

    /**
     * Updates an existing entity.
     *
     * @param entity the entity to update
     * @return the number of rows affected
     */
    int update(@Nonnull E entity);

    /**
     * Deletes an entity by its ID.
     *
     * @param id the entity ID
     * @return the number of rows affected
     */
    int delete(@Nonnull I id);

    /**
     * Queries entities with pagination and sorting.
     *
     * @param condition  the condition to filter entities
     * @param order      the order field to sort entities
     * @param pageNumber the page number (0-based)
     * @param pageSize   the number of items per page
     * @return a page of projection records
     */
    @Nonnull
    Page<P> query(@Nonnull Condition condition, @Nonnull OrderField<?> order, int pageNumber, int pageSize);

}