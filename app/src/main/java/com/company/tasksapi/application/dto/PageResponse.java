package com.company.tasksapi.application.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Paginated API response wrapper")
public record PageResponse<T>(
        @Schema(description = "Items in the current page")
        List<T> content,

        @Schema(description = "Current page number (0-based)", example = "0")
        int page,

        @Schema(description = "Number of items per page", example = "20")
        int size,

        @Schema(description = "Total number of elements", example = "150")
        long totalElements,

        @Schema(description = "Total number of pages", example = "8")
        int totalPages,

        @Schema(description = "Whether this is the first page")
        boolean first,

        @Schema(description = "Whether this is the last page")
        boolean last) {

    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) totalElements / (double) size);
        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                page == 0,
                (long) (page + 1) * size >= totalElements);
    }
}
