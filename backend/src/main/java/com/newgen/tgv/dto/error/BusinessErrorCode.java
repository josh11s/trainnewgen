package com.newgen.tgv.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Error - BusinessErrorCode", description = "Standardized business error codes for client translation and UI handling")
public enum BusinessErrorCode {

    @Schema(description = "The booking request exceeds the maximum allowed limit of 9 passengers")
    MAX_PASSENGERS_EXCEEDED("error.business.max_passengers_exceeded"),

    @Schema(description = "Requested travel date is in the past")
    DATE_IN_THE_PAST("error.business.date_in_the_past"),

    @Schema(description = "No seats remaining on this train")
    NO_SEATS_AVAILABLE("error.business.no_seats_available"),

    @Schema(description = "The requested seat is already locked or booked by another traveler")
    SEAT_ALREADY_RESERVED("error.business.seat_already_reserved");

    private final String code;

    BusinessErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}
