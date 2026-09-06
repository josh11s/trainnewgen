package com.newgen.tgv.controller;

import com.newgen.tgv.dto.TripSearchResponse;
import com.newgen.tgv.service.TripService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripSearchResponse>> searchTrips(
            @RequestParam("origin") String origin,
            @RequestParam("destination") String destination,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(name = "adults", defaultValue = "1") int adults,
            @RequestParam(name = "children", defaultValue = "0") int children
    ) {
        List<TripSearchResponse> results = tripService.searchTrips(origin, destination, date, adults, children);
        return ResponseEntity.ok(results);
    }
}
