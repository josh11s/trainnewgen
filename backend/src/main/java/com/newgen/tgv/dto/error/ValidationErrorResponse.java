package com.newgen.tgv.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.List;

@Schema(name = "Error - ValidationErrorResponse", description = "RFC 7807 problem details for input validation errors (HTTP 400)")
public record ValidationErrorResponse(
        @Schema(description = "URI reference identifying the problem type", example = "https://api.newgentgv.com/errors/validation-error")
        URI type,

        @Schema(description = "Short summary of the problem type", example = "Validation Error")
        String title,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Human-readable explanation specific to this occurrence", example = "Request validation failed")
        String detail,

        @Schema(description = "URI reference identifying the specific occurrence of the problem", example = "/api/v1/trips/search")
        URI instance,

        @Schema(description = "List of invalid parameters with corresponding constraint and translation keys")
        List<InvalidParamDetail> invalidParams
) {}
