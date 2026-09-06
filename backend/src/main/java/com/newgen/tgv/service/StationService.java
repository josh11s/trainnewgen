package com.newgen.tgv.service;

import com.newgen.tgv.dto.StationResponse;
import com.newgen.tgv.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<StationResponse> getAllStations() {
        return stationRepository.findAll().stream()
                .map(s -> new StationResponse(s.getCode(), s.getName(), s.getCity()))
                .toList();
    }
}
