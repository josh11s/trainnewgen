package com.newgen.tgv.service;

import com.newgen.tgv.dto.AvailableSeatsResponse;
import com.newgen.tgv.dto.StationResponse;
import com.newgen.tgv.dto.TripSearchResponse;
import com.newgen.tgv.model.Station;
import com.newgen.tgv.model.Trip;
import com.newgen.tgv.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TripService {

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<TripSearchResponse> searchTrips(String origin, String destination, LocalDate date, int adults, int children) {
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(LocalTime.MAX);

        int totalPassengers = Math.max(1, adults + children);

        List<Trip> trips = tripRepository.searchTrips(origin, destination, startTime, endTime);

        return trips.stream()
                .filter(trip -> (trip.getStandardSeatsAvailable() + trip.getFirstSeatsAvailable()) >= totalPassengers)
                .map(this::mapToTripSearchResponse)
                .toList();
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
