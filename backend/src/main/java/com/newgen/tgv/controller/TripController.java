package com.newgen.tgv.controller;

import com.newgen.tgv.dto.TripPageResponse;
import com.newgen.tgv.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import com.newgen.tgv.dto.error.BusinessErrorResponse;
import com.newgen.tgv.dto.error.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Trips", description = "Operations related to high-speed train journeys and schedules")
@Validated
@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @Operation(
            summary = "Search available trips with pagination",
            description = "Retrieves available TGV trips matching departure and arrival stations, travel date, and passenger count with paginated results."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search results page retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TripPageResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input parameters (e.g. invalid date format, adults < 1, children < 0)",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ValidationErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Business rule violation (e.g. passenger quota exceeded)",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = BusinessErrorResponse.class)
                    )
            )
    })
    @GetMapping("/search")
    public ResponseEntity<TripPageResponse> searchTrips(
            @Parameter(description = "Departure station code (e.g. FRPAR)", example = "FRPAR")
            @RequestParam("origin") @NotBlank(message = "Origin station code is required") String origin,

            @Parameter(description = "Arrival station code (e.g. FRRNS)", example = "FRRNS")
            @RequestParam("destination") @NotBlank(message = "Destination station code is required") String destination,

            @Parameter(description = "Travel date (YYYY-MM-DD)", example = "2026-09-10")
            @RequestParam("date") @NotNull(message = "Date is required") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

            @Parameter(description = "Number of adult passengers (minimum 1)", example = "1")
            @RequestParam("adults") @NotNull(message = "Number of adults is required") @Min(value = 1, message = "At least 1 adult passenger is required") Integer adults,

            @Parameter(description = "Number of child passengers (0 to 9)", example = "0")
            @RequestParam(name = "children", defaultValue = "0") @Min(value = 0, message = "Number of children cannot be negative") int children,

            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(name = "page", defaultValue = "0") @Min(value = 0, message = "Page index must be greater than or equal to 0") int page,

            @Parameter(description = "Page size (number of items per page, 1 to 50)", example = "5")
            @RequestParam(name = "size", defaultValue = "5") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 50, message = "Page size cannot exceed 50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("departureTime").ascending());
        TripPageResponse results = tripService.searchTrips(origin, destination, date, adults, children, pageable);
        return ResponseEntity.ok(results);
    }
}
