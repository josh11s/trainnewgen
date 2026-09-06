package com.newgen.tgv.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Remaining available seats breakdown")
public record AvailableSeatsResponse(
        @Schema(description = "Available seats in 2nd / Standard Class", example = "180")
        Integer standardClass,

        @Schema(description = "Available seats in 1st Class", example = "50")
        Integer firstClass
) {}
