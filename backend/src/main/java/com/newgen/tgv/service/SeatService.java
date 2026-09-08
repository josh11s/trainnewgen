package com.newgen.tgv.service;

import com.newgen.tgv.dto.StationResponse;
import com.newgen.tgv.dto.seat.*;
import com.newgen.tgv.model.*;
import com.newgen.tgv.repository.SeatRepository;
import com.newgen.tgv.repository.TripRepository;
import com.newgen.tgv.exception.SeatAlreadyReservedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeatService {

    public static final int SEATS_PER_COACH = 30;

    private final SeatRepository seatRepository;
    private final TripRepository tripRepository;
    private final TransactionTemplate transactionTemplate;

    public SeatService(SeatRepository seatRepository, TripRepository tripRepository, TransactionTemplate transactionTemplate) {
        this.seatRepository = seatRepository;
        this.tripRepository = tripRepository;
        this.transactionTemplate = transactionTemplate;
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

    public PaymentResponse processPayment(Long tripId, List<Long> seatIds) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found with id " + tripId));

        if (seatIds == null || seatIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one seat must be selected");
        }

        // 1. Transaction 1: Put seats on HOLD immediately and commit so concurrent users see LOCKED right away
        try {
            transactionTemplate.execute(status -> {
                // 1a. Release expired locks
                seatRepository.releaseExpiredLocksForTrip(tripId, LocalDateTime.now());

                // 1b. Fetch seats
                List<Seat> seats = seatRepository.findAllById(seatIds);
                if (seats.size() != seatIds.size()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Un ou plusieurs sièges sélectionnés sont introuvables");
                }

                // 1c. Concurrency check: verify each seat belongs to this trip
                for (Seat seat : seats) {
                    if (!seat.getTrip().getId().equals(tripId)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le siège " + seat.getSeatCode() + " n'appartient pas au voyage " + tripId);
                    }
                }

                // 1d. If at least 1 seat is already reserved / locked by another traveler, throw business exception
                List<Seat> unavailable = seats.stream()
                        .filter(s -> s.getStatus() != SeatStatus.AVAILABLE)
                        .toList();

                if (!unavailable.isEmpty()) {
                    String seatCodes = unavailable.stream()
                            .map(Seat::getSeatCode)
                            .collect(Collectors.joining(", "));
                    List<Map<String, Object>> details = unavailable.stream()
                            .map(s -> Map.<String, Object>of(
                                    "id", s.getId(),
                                    "seatCode", s.getSeatCode(),
                                    "status", s.getStatus().name()
                            ))
                            .toList();
                    throw new SeatAlreadyReservedException(seatCodes, details);
                }

                // 1e. Put on HOLD (LOCKED) immediately
                LocalDateTime holdExpiry = LocalDateTime.now().plusSeconds(180);
                for (Seat seat : seats) {
                    seat.setStatus(SeatStatus.LOCKED);
                    seat.setLockExpiresAt(holdExpiry);
                }
                seatRepository.saveAllAndFlush(seats);
                return null;
            });
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException | jakarta.persistence.OptimisticLockException ex) {
            // Local domain catch: check fresh DB state to find which seats were taken and their statuses (LOCKED/BOOKED)
            List<Seat> freshSeats = seatRepository.findAllById(seatIds);
            List<Seat> unavailable = freshSeats.stream()
                    .filter(s -> s.getStatus() != SeatStatus.AVAILABLE)
                    .toList();

            if (unavailable.isEmpty()) {
                unavailable = freshSeats;
            }

            String seatCodes = unavailable.stream()
                    .map(Seat::getSeatCode)
                    .collect(Collectors.joining(", "));

            List<Map<String, Object>> details = unavailable.stream()
                    .map(s -> Map.<String, Object>of(
                            "id", s.getId(),
                            "seatCode", s.getSeatCode(),
                            "status", s.getStatus().name()
                    ))
                    .toList();

            throw new SeatAlreadyReservedException(seatCodes, details);
        }

        // 2. Simulate payment latency: hold for 3 seconds! (runs outside any DB transaction to avoid locking rows/connections)
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 3. Transaction 2: Transition to BOOKED and return response
        return transactionTemplate.execute(status -> {
            List<Seat> seats = seatRepository.findAllById(seatIds);
            BigDecimal firstPrice = trip.getFirstClassPrice();
            BigDecimal standardPrice = trip.getBasePrice();
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<String> bookedCodes = new ArrayList<>();

            for (Seat seat : seats) {
                seat.setStatus(SeatStatus.BOOKED);
                seat.setLockExpiresAt(null);
                bookedCodes.add(seat.getSeatCode());

                BigDecimal price = (seat.getCoachClass() == CoachClass.FIRST) ? firstPrice : standardPrice;
                totalAmount = totalAmount.add(price);
            }
            seatRepository.saveAll(seats);

            String bookingReference = "NGT-" + java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();

            return new com.newgen.tgv.dto.seat.PaymentResponse(
                    bookingReference,
                    trip.getId(),
                    trip.getTrainNumber(),
                    bookedCodes,
                    totalAmount,
                    LocalDateTime.now(),
                    "CONFIRMED"
            );
        });
    }
}
