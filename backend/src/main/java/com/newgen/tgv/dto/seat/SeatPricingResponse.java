package com.newgen.tgv.dto.seat;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Trip pricing per class")
public record SeatPricingResponse(
        @Schema(description = "Second / standard class price", example = "35.00")
        BigDecimal standard,

        @Schema(description = "First class price", example = "55.00")
        BigDecimal first
) {}
