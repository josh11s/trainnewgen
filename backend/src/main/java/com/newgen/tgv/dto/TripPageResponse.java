package com.newgen.tgv.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(name = "Trip - PageResponse", description = "Paginated list of available TGV trips")
public record TripPageResponse(
        @Schema(description = "List of train trips available in the current page")
        List<TripSearchResponse> content,

        @Schema(description = "Zero-based page index", example = "0")
        int page,

        @Schema(description = "Number of items requested per page", example = "5")
        int size,

        @Schema(description = "Total number of elements matching search criteria", example = "20")
        long totalElements,

        @Schema(description = "Total number of available pages", example = "4")
        int totalPages,

        @Schema(description = "True if this is the first page", example = "true")
        boolean first,

        @Schema(description = "True if this is the last page", example = "false")
        boolean last
) {
    public static TripPageResponse of(Page<TripSearchResponse> springPage) {
        return new TripPageResponse(
                springPage.getContent(),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages(),
                springPage.isFirst(),
                springPage.isLast()
        );
    }
}
