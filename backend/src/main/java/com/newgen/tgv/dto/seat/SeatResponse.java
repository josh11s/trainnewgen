package com.newgen.tgv.dto.seat;

import com.newgen.tgv.model.CoachClass;
import com.newgen.tgv.model.SeatPosition;
import com.newgen.tgv.model.SeatStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Individual seat details")
public record SeatResponse(
        @Schema(description = "Seat database ID", example = "101")
        Long id,

        @Schema(description = "Seat number inside the coach (1 to 30)", example = "1")
        Integer seatNumber,

        @Schema(description = "Formatted seat code (e.g. 1-01, 2-15)", example = "1-01")
        String seatCode,

        @Schema(description = "Coach number (1 or 2)", example = "1")
        Integer coachNumber,

        @Schema(description = "Coach travel class (FIRST or STANDARD)", example = "FIRST")
        CoachClass coachClass,

        @Schema(description = "Position in coach (WINDOW, AISLE, SOLO, DUO)", example = "WINDOW")
        SeatPosition position,

        @Schema(description = "Current seat availability status (AVAILABLE, LOCKED, BOOKED)", example = "AVAILABLE")
        SeatStatus status,

        @Schema(description = "Calculated seat price in EUR", example = "55.00")
        BigDecimal price
) {}
