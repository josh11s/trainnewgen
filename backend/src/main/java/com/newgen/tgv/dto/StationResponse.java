package com.newgen.tgv.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Station - StationResponse", description = "Railway station information")
public record StationResponse(
        @Schema(description = "Unique station UIC/IATA code", example = "FRPAR")
        String code,

        @Schema(description = "Station official name", example = "Paris Montparnasse")
        String name,

        @Schema(description = "City location", example = "Paris")
        String city
) {}
