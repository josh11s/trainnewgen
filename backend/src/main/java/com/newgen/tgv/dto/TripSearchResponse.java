package com.newgen.tgv.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripSearchResponse(
        Long id,
        String trainNumber,
        StationResponse departureStation,
        StationResponse arrivalStation,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        Integer durationMinutes,
        BigDecimal startingPrice,
        AvailableSeatsResponse availableSeats
) {}
