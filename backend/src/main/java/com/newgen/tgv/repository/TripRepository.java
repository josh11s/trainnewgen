package com.newgen.tgv.repository;

import com.newgen.tgv.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("SELECT t FROM Trip t " +
           "WHERE UPPER(t.departureStation.code) = UPPER(:originCode) " +
           "AND UPPER(t.arrivalStation.code) = UPPER(:destinationCode) " +
           "AND t.departureTime BETWEEN :startTime AND :endTime " +
           "ORDER BY t.departureTime ASC")
    List<Trip> searchTrips(
            @Param("originCode") String originCode,
            @Param("destinationCode") String destinationCode,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
