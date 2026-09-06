package com.newgen.tgv.controller;

import com.newgen.tgv.dto.StationResponse;
import com.newgen.tgv.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Stations", description = "Operations related to high-speed railway stations")
@RestController
@RequestMapping("/api/v1/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @Operation(summary = "Get all railway stations", description = "Retrieves the full list of operational NewGen TGV stations.")
    @ApiResponse(
            responseCode = "200",
            description = "Stations list retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = StationResponse.class))
            )
    )
    @GetMapping
    public ResponseEntity<List<StationResponse>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }
}
