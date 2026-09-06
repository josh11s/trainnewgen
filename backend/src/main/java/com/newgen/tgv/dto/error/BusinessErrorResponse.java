package com.newgen.tgv.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.Map;

@Schema(name = "Error - BusinessErrorResponse", description = "RFC 7807 problem details for business rule violations (HTTP 422)")
public record BusinessErrorResponse(
        @Schema(description = "URI reference identifying the problem type", example = "https://api.newgentgv.com/errors/error.business.max_passengers_exceeded")
        URI type,

        @Schema(description = "Short summary of the problem type", example = "Business Rule Violation")
        String title,

        @Schema(description = "HTTP status code", example = "422")
        int status,

        @Schema(description = "Human-readable explanation specific to this occurrence", example = "Booking cannot exceed 9 passengers per request")
        String detail,

        @Schema(description = "URI reference identifying the specific occurrence of the problem", example = "/api/v1/trips/search")
        URI instance,

        @Schema(description = "Standardized business error code for client-side translation")
        BusinessErrorCode errorCode,

        @Schema(description = "Contextual parameters associated with the rule violation", example = "{\"maxAllowed\": 9, \"requested\": 10}")
        Map<String, Object> params
) {}
