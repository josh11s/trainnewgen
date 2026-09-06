package com.newgen.tgv;

import com.newgen.tgv.dto.TripSearchResponse;
import com.newgen.tgv.model.Station;
import com.newgen.tgv.model.Trip;
import com.newgen.tgv.repository.TripRepository;
import com.newgen.tgv.service.TripService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripService tripService;

    @Test
    void shouldReturnTripsMatchingCriteria() {
        Station paris = new Station("FRPAR", "Paris Montparnasse", "Paris");
        Station rennes = new Station("FRRNS", "Rennes", "Rennes");
        LocalDate date = LocalDate.of(2026, 9, 10);
        LocalDateTime depTime = date.atTime(8, 0);

        Trip trip = new Trip(
                "NGT-8101", paris, rennes, depTime,
                depTime.plusMinutes(90), 90, new BigDecimal("35.00"),
                100, 30
        );

        when(tripRepository.searchTrips(eq("FRPAR"), eq("FRRNS"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(trip));

        List<TripSearchResponse> responses = tripService.searchTrips("FRPAR", "FRRNS", date, 2, 1);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("NGT-8101", responses.get(0).trainNumber());
        assertEquals("FRPAR", responses.get(0).departureStation().code());
        assertEquals("FRRNS", responses.get(0).arrivalStation().code());
        assertEquals(100, responses.get(0).availableSeats().standardClass());
    }

    @Test
    void shouldExcludeTripsWithoutEnoughAvailableSeats() {
        Station paris = new Station("FRPAR", "Paris Montparnasse", "Paris");
        Station rennes = new Station("FRRNS", "Rennes", "Rennes");
        LocalDate date = LocalDate.of(2026, 9, 10);
        LocalDateTime depTime = date.atTime(8, 0);

        Trip fullTrip = new Trip(
                "NGT-8101", paris, rennes, depTime,
                depTime.plusMinutes(90), 90, new BigDecimal("35.00"),
                1, 0
        );

        when(tripRepository.searchTrips(eq("FRPAR"), eq("FRRNS"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(fullTrip));

        // Requesting 2 adults + 1 child = 3 passengers, but only 1 seat available
        List<TripSearchResponse> responses = tripService.searchTrips("FRPAR", "FRRNS", date, 2, 1);

        assertTrue(responses.isEmpty());
    }
}
