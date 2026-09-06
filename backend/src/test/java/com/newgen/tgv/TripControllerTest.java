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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
}
