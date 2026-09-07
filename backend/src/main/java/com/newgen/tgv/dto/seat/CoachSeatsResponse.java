package com.newgen.tgv.dto.seat;

import com.newgen.tgv.model.CoachClass;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Train coach layout with seats")
public record CoachSeatsResponse(
        @Schema(description = "Coach number (e.g. 1, 2)", example = "1")
        Integer coachNumber,

        @Schema(description = "Coach travel class (FIRST or STANDARD)", example = "FIRST")
        CoachClass coachClass,

        @Schema(description = "Base ticket price for this coach class in EUR", example = "55.00")
        BigDecimal price,

        @Schema(description = "Total capacity of the coach", example = "30")
        Integer totalSeats,

        @Schema(description = "Remaining available seats in this coach", example = "30")
        Integer availableSeatsCount,

        @Schema(description = "List of 30 seats in this coach")
        List<SeatResponse> seats
) {}
