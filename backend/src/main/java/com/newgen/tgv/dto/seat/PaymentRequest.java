package com.newgen.tgv.dto.seat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Payment simulation request with chosen seat IDs")
public record PaymentRequest(
        @Schema(description = "List of seat IDs to book and pay for", example = "[1, 2]")
        @NotEmpty(message = "At least one seat ID must be provided")
        List<Long> seatIds
) {}
