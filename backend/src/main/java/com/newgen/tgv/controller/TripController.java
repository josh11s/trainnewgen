package com.newgen.tgv.controller;

import com.newgen.tgv.dto.TripSearchResponse;
import com.newgen.tgv.service.TripService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripSearchResponse>> searchTrips(
            @RequestParam("origin") @NotBlank(message = "Origin station code is required") String origin,
            @RequestParam("destination") @NotBlank(message = "Destination station code is required") String destination,
            @RequestParam("date") @NotNull(message = "Date is required") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("adults") @NotNull(message = "Number of adults is required") @Min(value = 1, message = "At least 1 adult passenger is required") Integer adults,
            @RequestParam(name = "children", defaultValue = "0") @Min(value = 0, message = "Number of children cannot be negative") int children
    ) {
        List<TripSearchResponse> results = tripService.searchTrips(origin, destination, date, adults, children);
        return ResponseEntity.ok(results);
    }
}
