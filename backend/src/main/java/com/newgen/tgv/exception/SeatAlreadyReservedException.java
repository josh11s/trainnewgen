package com.newgen.tgv.exception;

import com.newgen.tgv.dto.error.BusinessErrorCode;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class SeatAlreadyReservedException extends BusinessRuleException {

    private final List<Map<String, Object>> unavailableSeats;

    public SeatAlreadyReservedException(String seatCodes) {
        this(seatCodes, List.of());
    }

    public SeatAlreadyReservedException(String seatCodes, List<Map<String, Object>> unavailableSeats) {
        super(
                BusinessErrorCode.SEAT_ALREADY_RESERVED,
                "Seat " + seatCodes + " is already reserved by another traveler",
                HttpStatus.CONFLICT,
                Map.of(
                        "seats", seatCodes,
                        "count", unavailableSeats != null && !unavailableSeats.isEmpty() ? unavailableSeats.size() : (seatCodes.contains(",") ? 2 : 1)
                )
        );
        this.unavailableSeats = unavailableSeats != null ? unavailableSeats : List.of();
    }

    public List<Map<String, Object>> getUnavailableSeats() {
        return unavailableSeats;
    }
}
