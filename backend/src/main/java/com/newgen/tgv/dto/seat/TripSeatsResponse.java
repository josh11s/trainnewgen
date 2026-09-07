package com.newgen.tgv.dto.seat;

import com.newgen.tgv.dto.StationResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Full train seat map and availability for a trip")
public record TripSeatsResponse(
        @Schema(description = "Trip unique identifier", example = "1")
        Long tripId,

        @Schema(description = "Commercial train number", example = "NGT-8101")
        String trainNumber,

        @Schema(description = "Departure station")
        StationResponse departureStation,

        @Schema(description = "Arrival station")
        StationResponse arrivalStation,

        @Schema(description = "Departure timestamp", example = "2026-09-10T08:00:00")
        LocalDateTime departureTime,

        @Schema(description = "Arrival timestamp", example = "2026-09-10T09:30:00")
        LocalDateTime arrivalTime,

        @Schema(description = "Pricing details for both travel classes")
        SeatPricingResponse pricing,

        @Schema(description = "Total seats in train", example = "60")
        Integer totalSeats,

        @Schema(description = "Total available seats across all coaches", example = "60")
        Integer availableSeats,

        @Schema(description = "Coaches composing the train (Coach 1 First, Coach 2 Standard)")
        List<CoachSeatsResponse> coaches
) {}
