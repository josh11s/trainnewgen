package com.newgen.tgv.dto.seat;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Payment simulation response with confirmation details")
public record PaymentResponse(
        @Schema(description = "Booking reference code", example = "NGT-8F2K")
        String bookingReference,

        @Schema(description = "Trip ID", example = "1")
        Long tripId,

        @Schema(description = "Commercial train number", example = "NGT-8202")
        String trainNumber,

        @Schema(description = "Codes of the booked seats", example = "[\"1-01\", \"1-02\"]")
        List<String> bookedSeats,

        @Schema(description = "Total amount paid in EUR", example = "117.00")
        BigDecimal totalAmount,

        @Schema(description = "Confirmation timestamp", example = "2026-09-07T22:30:00")
        LocalDateTime confirmedAt,

        @Schema(description = "Booking status", example = "CONFIRMED")
        String status
) {}
