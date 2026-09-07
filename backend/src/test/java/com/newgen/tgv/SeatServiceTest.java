package com.newgen.tgv;

import com.newgen.tgv.dto.seat.CoachSeatsResponse;
import com.newgen.tgv.dto.seat.TripSeatsResponse;
import com.newgen.tgv.model.*;
import com.newgen.tgv.repository.SeatRepository;
import com.newgen.tgv.repository.TripRepository;
import com.newgen.tgv.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private SeatService seatService;

    private Trip testTrip;

    @BeforeEach
    void setUp() {
        Station paris = new Station("FRPAR", "Paris Montparnasse", "Paris");
        Station rennes = new Station("FRRNS", "Rennes", "Rennes");
        testTrip = new Trip(
                "NGT-8101", paris, rennes,
                LocalDateTime.of(2026, 9, 10, 8, 0),
                LocalDateTime.of(2026, 9, 10, 9, 30),
                90, new BigDecimal("35.00"), new BigDecimal("55.00"), 30, 30
        );
        testTrip.setId(1L);
    }

    @Test
    void shouldGenerate60SeatsWhenNoSeatsExist() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(seatRepository.findByTripIdOrderByCoachNumberAscSeatNumberAsc(1L))
                .thenReturn(Collections.emptyList());

        when(seatRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TripSeatsResponse response = seatService.getSeatsForTrip(1L);

        assertNotNull(response);
        assertEquals(1L, response.tripId());
        assertEquals("NGT-8101", response.trainNumber());
        assertEquals(60, response.totalSeats());
        assertEquals(60, response.availableSeats());
        assertEquals(new BigDecimal("35.00"), response.pricing().standard());
        assertEquals(new BigDecimal("55.00"), response.pricing().first());

        assertEquals(2, response.coaches().size());

        // Coach 1 - First Class
        CoachSeatsResponse coach1 = response.coaches().get(0);
        assertEquals(1, coach1.coachNumber());
        assertEquals(CoachClass.FIRST, coach1.coachClass());
        assertEquals(new BigDecimal("55.00"), coach1.price());
        assertEquals(30, coach1.totalSeats());
        assertEquals(30, coach1.availableSeatsCount());
        assertEquals(30, coach1.seats().size());
        assertEquals("1-01", coach1.seats().get(0).seatCode());
        assertEquals("1-30", coach1.seats().get(29).seatCode());
        assertEquals(new BigDecimal("55.00"), coach1.seats().get(0).price());

        // Coach 2 - Standard Class
        CoachSeatsResponse coach2 = response.coaches().get(1);
        assertEquals(2, coach2.coachNumber());
        assertEquals(CoachClass.STANDARD, coach2.coachClass());
        assertEquals(new BigDecimal("35.00"), coach2.price());
        assertEquals(30, coach2.totalSeats());
        assertEquals(30, coach2.availableSeatsCount());
        assertEquals(30, coach2.seats().size());
        assertEquals("2-01", coach2.seats().get(0).seatCode());
        assertEquals("2-30", coach2.seats().get(29).seatCode());
        assertEquals(new BigDecimal("35.00"), coach2.seats().get(0).price());

        verify(seatRepository).saveAll(any());
        verify(seatRepository).releaseExpiredLocksForTrip(eq(1L), any());
    }

    @Test
    void shouldReturnExistingSeatsWithAccurateAvailability() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));

        List<Seat> existingSeats = new ArrayList<>();
        // 30 first class seats (1 BOOKED, 1 LOCKED, 28 AVAILABLE)
        for (int i = 1; i <= 30; i++) {
            SeatStatus status = (i == 1) ? SeatStatus.BOOKED : (i == 2 ? SeatStatus.LOCKED : SeatStatus.AVAILABLE);
            Seat s = new Seat(testTrip, 1, CoachClass.FIRST, i, String.format("1-%02d", i), SeatPosition.SOLO, status);
            s.setId((long) i);
            existingSeats.add(s);
        }
        // 30 standard class seats (all AVAILABLE)
        for (int i = 1; i <= 30; i++) {
            Seat s = new Seat(testTrip, 2, CoachClass.STANDARD, i, String.format("2-%02d", i), SeatPosition.WINDOW, SeatStatus.AVAILABLE);
            s.setId((long) (30 + i));
            existingSeats.add(s);
        }

        when(seatRepository.findByTripIdOrderByCoachNumberAscSeatNumberAsc(1L)).thenReturn(existingSeats);

        TripSeatsResponse response = seatService.getSeatsForTrip(1L);

        assertNotNull(response);
        assertEquals(60, response.totalSeats());
        assertEquals(58, response.availableSeats()); // 28 + 30
        assertEquals(28, response.coaches().get(0).availableSeatsCount());
        assertEquals(30, response.coaches().get(1).availableSeatsCount());

        assertEquals(SeatStatus.BOOKED, response.coaches().get(0).seats().get(0).status());
        assertEquals(SeatStatus.LOCKED, response.coaches().get(0).seats().get(1).status());
        assertEquals(SeatStatus.AVAILABLE, response.coaches().get(0).seats().get(2).status());

        verify(seatRepository, never()).saveAll(any());
    }

    @Test
    void shouldThrow404WhenTripNotFound() {
        when(tripRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seatService.getSeatsForTrip(999L));
        assertEquals(404, ex.getStatusCode().value());
    }
}
