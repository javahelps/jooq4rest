package com.javahelps.jooq4rest.page;

import java.util.List;

/**
 * A record representing a paginated response.
 *
 * @param <T>           the type of the content in the page
 * @param content       the list of content items in the current page
 * @param pageNumber    the current page number (0-based)
 * @param pageSize      the number of items per page
 * @param totalPages    the total number of pages
 * @param totalElements the total number of elements across all pages
 */
public record Page<T>(List<T> content,
                      int pageNumber,
                      int pageSize,
                      int totalPages,
                      long totalElements) {

    /**
     * Creates an empty page with the specified page size.
     *
     * @param pageSize the number of items per page
     * @param <T>      the type of the content in the page
     * @return an empty page
     */
    public static <T> Page<T> empty(int pageSize) {
        return new Page<>(List.of(), 0, pageSize, 0, 0);
    }
}