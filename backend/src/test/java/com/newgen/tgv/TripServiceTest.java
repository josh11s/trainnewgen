package com.newgen.tgv;

import com.newgen.tgv.dto.TripPageResponse;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripService tripService;

    @Test
    void shouldReturnTripsMatchingCriteriaWithPagination() {
        Station paris = new Station("FRPAR", "Paris Montparnasse", "Paris");
        Station rennes = new Station("FRRNS", "Rennes", "Rennes");
        LocalDate date = LocalDate.of(2026, 9, 10);
        LocalDateTime depTime = date.atTime(8, 0);

        Trip trip = new Trip(
                "NGT-8101", paris, rennes, depTime,
                depTime.plusMinutes(90), 90, new BigDecimal("35.00"),
                100, 30
        );

        Pageable pageable = PageRequest.of(0, 5);
        when(tripRepository.searchTrips(eq("FRPAR"), eq("FRRNS"), any(LocalDateTime.class), any(LocalDateTime.class), eq(3), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(trip), pageable, 1));

        TripPageResponse response = tripService.searchTrips("FRPAR", "FRRNS", date, 2, 1, pageable);

        assertNotNull(response);
        assertEquals(1, response.totalElements());
        assertEquals(1, response.totalPages());
        assertEquals(0, response.page());
        assertEquals(5, response.size());
        assertEquals(1, response.content().size());
        assertEquals("NGT-8101", response.content().get(0).trainNumber());
        assertEquals("FRPAR", response.content().get(0).departureStation().code());
        assertEquals("FRRNS", response.content().get(0).arrivalStation().code());
    }

    @Test
    void shouldThrowBusinessRuleExceptionWhenPassengersExceedLimit() {
        LocalDate date = LocalDate.of(2026, 9, 10);
        Pageable pageable = PageRequest.of(0, 5);

        com.newgen.tgv.exception.BusinessRuleException ex = assertThrows(
                com.newgen.tgv.exception.BusinessRuleException.class,
                () -> tripService.searchTrips("FRPAR", "FRRNS", date, 8, 2, pageable)
        );

        assertEquals(com.newgen.tgv.dto.error.BusinessErrorCode.MAX_PASSENGERS_EXCEEDED, ex.getErrorCode());
        assertEquals("error.business.max_passengers_exceeded", ex.getErrorCodeValue());
        assertEquals(9, ex.getParams().get("maxAllowed"));
        assertEquals(10, ex.getParams().get("requested"));
    }
}
