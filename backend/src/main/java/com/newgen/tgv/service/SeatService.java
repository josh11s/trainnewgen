package com.newgen.tgv.service;

import com.newgen.tgv.dto.StationResponse;
import com.newgen.tgv.dto.seat.CoachSeatsResponse;
import com.newgen.tgv.dto.seat.SeatPricingResponse;
import com.newgen.tgv.dto.seat.SeatResponse;
import com.newgen.tgv.dto.seat.TripSeatsResponse;
import com.newgen.tgv.model.*;
import com.newgen.tgv.repository.SeatRepository;
import com.newgen.tgv.repository.TripRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    public static final int SEATS_PER_COACH = 30;

    private final SeatRepository seatRepository;
    private final TripRepository tripRepository;

    public SeatService(SeatRepository seatRepository, TripRepository tripRepository) {
        this.seatRepository = seatRepository;
        this.tripRepository = tripRepository;
    }

    @Transactional
    public TripSeatsResponse getSeatsForTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found with id " + tripId));

        // 1. Release any expired locks for this trip
        seatRepository.releaseExpiredLocksForTrip(tripId, LocalDateTime.now());

        // 2. Fetch seats or generate them on demand if not present yet
        List<Seat> seats = seatRepository.findByTripIdOrderByCoachNumberAscSeatNumberAsc(tripId);
        if (seats.isEmpty()) {
            seats = generateSeatsForTrip(trip);
        }

        // 3. Separate by coach and build DTOs
        BigDecimal firstPrice = trip.getFirstClassPrice();
        BigDecimal standardPrice = trip.getBasePrice();

        List<SeatResponse> coach1Seats = new ArrayList<>(SEATS_PER_COACH);
        List<SeatResponse> coach2Seats = new ArrayList<>(SEATS_PER_COACH);

        int coach1Available = 0;
        int coach2Available = 0;

        for (Seat s : seats) {
            BigDecimal price = (s.getCoachClass() == CoachClass.FIRST) ? firstPrice : standardPrice;
            SeatResponse seatResp = new SeatResponse(
                    s.getId(),
                    s.getSeatNumber(),
                    s.getSeatCode(),
                    s.getCoachNumber(),
                    s.getCoachClass(),
                    s.getPosition(),
                    s.getStatus(),
                    price
            );

            if (s.getCoachNumber() == 1) {
                coach1Seats.add(seatResp);
                if (s.getStatus() == SeatStatus.AVAILABLE) {
                    coach1Available++;
                }
            } else {
                coach2Seats.add(seatResp);
                if (s.getStatus() == SeatStatus.AVAILABLE) {
                    coach2Available++;
                }
            }
        }

        CoachSeatsResponse coach1 = new CoachSeatsResponse(
                1,
                CoachClass.FIRST,
                firstPrice,
                SEATS_PER_COACH,
                coach1Available,
                coach1Seats
        );

        CoachSeatsResponse coach2 = new CoachSeatsResponse(
                2,
                CoachClass.STANDARD,
                standardPrice,
                SEATS_PER_COACH,
                coach2Available,
                coach2Seats
        );

        Station dep = trip.getDepartureStation();
        Station arr = trip.getArrivalStation();
        StationResponse depResp = new StationResponse(dep.getCode(), dep.getName(), dep.getCity());
        StationResponse arrResp = new StationResponse(arr.getCode(), arr.getName(), arr.getCity());

        return new TripSeatsResponse(
                trip.getId(),
                trip.getTrainNumber(),
                depResp,
                arrResp,
                trip.getDepartureTime(),
                trip.getArrivalTime(),
                new SeatPricingResponse(standardPrice, firstPrice),
                seats.size(),
                coach1Available + coach2Available,
                List.of(coach1, coach2)
        );
    }

    @Transactional
    public List<Seat> generateSeatsForTrip(Trip trip) {
        List<Seat> seats = new ArrayList<>(SEATS_PER_COACH * 2);

        // Coach 1: 30 First Class Seats (Voiture 1)
        for (int i = 1; i <= SEATS_PER_COACH; i++) {
            String seatCode = String.format("1-%02d", i);
            SeatPosition pos = (i % 3 == 1) ? SeatPosition.SOLO : (i % 3 == 2 ? SeatPosition.AISLE : SeatPosition.WINDOW);
            seats.add(new Seat(trip, 1, CoachClass.FIRST, i, seatCode, pos, SeatStatus.AVAILABLE));
        }

        // Coach 2: 30 Standard Class Seats (Voiture 2)
        for (int i = 1; i <= SEATS_PER_COACH; i++) {
            String seatCode = String.format("2-%02d", i);
            SeatPosition pos = (i % 4 == 1 || i % 4 == 0) ? SeatPosition.WINDOW : SeatPosition.AISLE;
            seats.add(new Seat(trip, 2, CoachClass.STANDARD, i, seatCode, pos, SeatStatus.AVAILABLE));
        }

        return seatRepository.saveAll(seats);
    }
}
