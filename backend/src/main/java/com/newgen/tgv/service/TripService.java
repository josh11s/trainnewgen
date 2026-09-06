package com.newgen.tgv.service;

import com.newgen.tgv.dto.AvailableSeatsResponse;
import com.newgen.tgv.dto.StationResponse;
import com.newgen.tgv.dto.TripPageResponse;
import com.newgen.tgv.dto.TripSearchResponse;
import com.newgen.tgv.exception.BusinessRuleException;
import com.newgen.tgv.model.Station;
import com.newgen.tgv.model.Trip;
import com.newgen.tgv.repository.TripRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class TripService {

    public static final int MAX_PASSENGERS_PER_BOOKING = 9;

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public TripPageResponse searchTrips(
            String origin,
            String destination,
            LocalDate date,
            int adults,
            int children,
            Pageable pageable
    ) {
        int totalPassengers = adults + children;
        if (totalPassengers > MAX_PASSENGERS_PER_BOOKING) {
            throw new BusinessRuleException(
                    "error.business.max_passengers_exceeded",
                    "Booking cannot exceed " + MAX_PASSENGERS_PER_BOOKING + " passengers per request",
                    Map.of("maxAllowed", MAX_PASSENGERS_PER_BOOKING, "requested", totalPassengers)
            );
        }

        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(LocalTime.MAX);
        int passengerCount = Math.max(1, totalPassengers);

        Page<Trip> tripsPage = tripRepository.searchTrips(
                origin,
                destination,
                startTime,
                endTime,
                passengerCount,
                pageable
        );

        Page<TripSearchResponse> responsePage = tripsPage.map(this::mapToTripSearchResponse);
        return TripPageResponse.of(responsePage);
    }

    private TripSearchResponse mapToTripSearchResponse(Trip trip) {
        Station dep = trip.getDepartureStation();
        Station arr = trip.getArrivalStation();

        StationResponse departureResponse = new StationResponse(dep.getCode(), dep.getName(), dep.getCity());
        StationResponse arrivalResponse = new StationResponse(arr.getCode(), arr.getName(), arr.getCity());
        AvailableSeatsResponse seatsResponse = new AvailableSeatsResponse(
                trip.getStandardSeatsAvailable(),
                trip.getFirstSeatsAvailable()
        );

        return new TripSearchResponse(
                trip.getId(),
                trip.getTrainNumber(),
                departureResponse,
                arrivalResponse,
                trip.getDepartureTime(),
                trip.getArrivalTime(),
                trip.getDurationMinutes(),
                trip.getBasePrice(),
                seatsResponse
        );
    }
}
