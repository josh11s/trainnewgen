package com.newgen.tgv.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "Trip - SearchResponse", description = "Details of an available train trip")
public record TripSearchResponse(
        @Schema(description = "Trip unique identifier", example = "1")
        Long id,

        @Schema(description = "Train commercial number", example = "NGT-8101")
        String trainNumber,

        @Schema(description = "Departure station details")
        StationResponse departureStation,

        @Schema(description = "Arrival station details")
        StationResponse arrivalStation,

        @Schema(description = "Departure date and time (ISO-8601)", example = "2026-09-10T06:15:00")
        LocalDateTime departureTime,

        @Schema(description = "Arrival date and time (ISO-8601)", example = "2026-09-10T07:43:00")
        LocalDateTime arrivalTime,

        @Schema(description = "Journey duration in minutes", example = "88")
        Integer durationMinutes,

        @Schema(description = "Starting price in EUR", example = "29.00")
        BigDecimal startingPrice,

        @Schema(description = "Remaining available seats per class")
        AvailableSeatsResponse availableSeats
) {}
