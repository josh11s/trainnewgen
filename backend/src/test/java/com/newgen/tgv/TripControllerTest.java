package com.newgen.tgv;

import com.newgen.tgv.controller.TripController;
import com.newgen.tgv.dto.TripPageResponse;
import com.newgen.tgv.exception.BusinessRuleException;
import com.newgen.tgv.service.TripService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripController.class)
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TripService tripService;

    @MockitoBean
    private com.newgen.tgv.service.SeatService seatService;

    @Test
    void searchTrips_whenValidParameters_shouldReturn200() throws Exception {
        TripPageResponse response = new TripPageResponse(Collections.emptyList(), 0, 5, 0, 0, true, true);
        when(tripService.searchTrips(anyString(), anyString(), any(), anyInt(), anyInt(), any()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/trips/search")
                        .param("origin", "FRPAR")
                        .param("destination", "FRRNS")
                        .param("date", "2026-09-10")
                        .param("adults", "1")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5));
    }

    @Test
    void searchTrips_whenAdultsMissing_shouldReturn400WithProblemDetail() throws Exception {
        mockMvc.perform(get("/api/v1/trips/search")
                        .param("origin", "FRPAR")
                        .param("destination", "FRRNS")
                        .param("date", "2026-09-10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.invalidParams[0].name").value("adults"));
    }

    @Test
    void searchTrips_whenAdultsLessThanOne_shouldReturn400WithProblemDetail() throws Exception {
        mockMvc.perform(get("/api/v1/trips/search")
                        .param("origin", "FRPAR")
                        .param("destination", "FRRNS")
                        .param("date", "2026-09-10")
                        .param("adults", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.invalidParams[0].name").value("adults"))
                .andExpect(jsonPath("$.invalidParams[0].messageKey").value("validation.adults.min"));
    }

    @Test
    void searchTrips_whenChildrenNegative_shouldReturn400WithProblemDetail() throws Exception {
        mockMvc.perform(get("/api/v1/trips/search")
                        .param("origin", "FRPAR")
                        .param("destination", "FRRNS")
                        .param("date", "2026-09-10")
                        .param("adults", "1")
                        .param("children", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.invalidParams[0].name").value("children"))
                .andExpect(jsonPath("$.invalidParams[0].messageKey").value("validation.children.min"));
    }

    @Test
    void searchTrips_whenPassengersExceedLimit_shouldReturn422ProblemDetail() throws Exception {
        when(tripService.searchTrips(anyString(), anyString(), any(), anyInt(), anyInt(), any()))
                .thenThrow(new BusinessRuleException(
                        com.newgen.tgv.dto.error.BusinessErrorCode.MAX_PASSENGERS_EXCEEDED,
                        "Booking cannot exceed 9 passengers",
                        Map.of("maxAllowed", 9, "requested", 10)
                ));

        mockMvc.perform(get("/api/v1/trips/search")
                        .param("origin", "FRPAR")
                        .param("destination", "FRRNS")
                        .param("date", "2026-09-10")
                        .param("adults", "7")
                        .param("children", "3"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.errorCode").value("error.business.max_passengers_exceeded"))
                .andExpect(jsonPath("$.params.maxAllowed").value(9));
    }

    @Test
    void getTripSeats_whenTripExists_shouldReturn200WithCoachesAndSeats() throws Exception {
        com.newgen.tgv.dto.StationResponse dep = new com.newgen.tgv.dto.StationResponse("FRPAR", "Paris Montparnasse", "Paris");
        com.newgen.tgv.dto.StationResponse arr = new com.newgen.tgv.dto.StationResponse("FRRNS", "Rennes", "Rennes");
        com.newgen.tgv.dto.seat.SeatPricingResponse pricing = new com.newgen.tgv.dto.seat.SeatPricingResponse(new java.math.BigDecimal("35.00"), new java.math.BigDecimal("55.00"));

        com.newgen.tgv.dto.seat.SeatResponse seat1 = new com.newgen.tgv.dto.seat.SeatResponse(
                1L, 1, "1-01", 1, com.newgen.tgv.model.CoachClass.FIRST,
                com.newgen.tgv.model.SeatPosition.SOLO, com.newgen.tgv.model.SeatStatus.AVAILABLE, new java.math.BigDecimal("55.00")
        );
        com.newgen.tgv.dto.seat.CoachSeatsResponse coach1 = new com.newgen.tgv.dto.seat.CoachSeatsResponse(
                1, com.newgen.tgv.model.CoachClass.FIRST, new java.math.BigDecimal("55.00"), 30, 30, java.util.List.of(seat1)
        );
        com.newgen.tgv.dto.seat.CoachSeatsResponse coach2 = new com.newgen.tgv.dto.seat.CoachSeatsResponse(
                2, com.newgen.tgv.model.CoachClass.STANDARD, new java.math.BigDecimal("35.00"), 30, 30, java.util.Collections.emptyList()
        );

        com.newgen.tgv.dto.seat.TripSeatsResponse response = new com.newgen.tgv.dto.seat.TripSeatsResponse(
                1L, "NGT-8101", dep, arr,
                java.time.LocalDateTime.of(2026, 9, 10, 8, 0),
                java.time.LocalDateTime.of(2026, 9, 10, 9, 30),
                pricing, 60, 60, java.util.List.of(coach1, coach2)
        );

        when(seatService.getSeatsForTrip(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/trips/1/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(1))
                .andExpect(jsonPath("$.trainNumber").value("NGT-8101"))
                .andExpect(jsonPath("$.totalSeats").value(60))
                .andExpect(jsonPath("$.coaches.length()").value(2))
                .andExpect(jsonPath("$.coaches[0].coachNumber").value(1))
                .andExpect(jsonPath("$.coaches[0].coachClass").value("FIRST"))
                .andExpect(jsonPath("$.coaches[0].price").value(55.00))
                .andExpect(jsonPath("$.coaches[1].coachNumber").value(2))
                .andExpect(jsonPath("$.coaches[1].coachClass").value("STANDARD"))
                .andExpect(jsonPath("$.coaches[1].price").value(35.00));
    }

    @Test
    void getTripSeats_whenTripNotFound_shouldReturn404() throws Exception {
        when(seatService.getSeatsForTrip(999L))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Trip not found with id 999"
                ));

        mockMvc.perform(get("/api/v1/trips/999/seats"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void paySeats_whenSeatsAvailable_shouldReturn200WithConfirmedStatus() throws Exception {
        com.newgen.tgv.dto.seat.PaymentResponse response = new com.newgen.tgv.dto.seat.PaymentResponse(
                "NGT-REF123", 1L, "NGT-8101",
                java.util.List.of("1-01"),
                new java.math.BigDecimal("55.00"),
                java.time.LocalDateTime.now(),
                "CONFIRMED"
        );

        when(seatService.processPayment(eq(1L), any())).thenReturn(response);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/trips/1/pay")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"seatIds\": [101]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingReference").value("NGT-REF123"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.totalAmount").value(55.00));
    }

    @Test
    void paySeats_whenSeatConflict_shouldReturn409() throws Exception {
        when(seatService.processPayment(eq(1L), any()))
                .thenThrow(new com.newgen.tgv.exception.SeatAlreadyReservedException(
                        "1-01",
                        List.of(Map.of("id", 101L, "seatCode", "1-01", "status", "LOCKED"))
                ));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/trips/1/pay")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"seatIds\": [101]}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.errorCode").value("error.business.seat_already_reserved"))
                .andExpect(jsonPath("$.params.seats").value("1-01"))
                .andExpect(jsonPath("$.unavailableSeats[0].seatCode").value("1-01"))
                .andExpect(jsonPath("$.unavailableSeats[0].status").value("LOCKED"));
    }
}
