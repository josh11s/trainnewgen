package com.newgen.tgv.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Error - InvalidParamDetail", description = "Details of an invalid request parameter")
public record InvalidParamDetail(
        @Schema(description = "Name of the invalid parameter/field", example = "date")
        String name,

        @Schema(description = "Constraint violation code", example = "TypeMismatch")
        String code,

        @Schema(description = "Translation key for client-side localization", example = "validation.date.type")
        String messageKey,

        @Schema(description = "Default technical error message in English", example = "Invalid value for parameter 'date'")
        String message
) {}
