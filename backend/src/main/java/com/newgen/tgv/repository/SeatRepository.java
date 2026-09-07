package com.newgen.tgv.repository;

import com.newgen.tgv.model.CoachClass;
import com.newgen.tgv.model.Seat;
import com.newgen.tgv.model.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByTripIdOrderByCoachNumberAscSeatNumberAsc(Long tripId);

    boolean existsByTripId(Long tripId);

    long countByTripIdAndStatus(Long tripId, SeatStatus status);

    long countByTripIdAndCoachClassAndStatus(Long tripId, CoachClass coachClass, SeatStatus status);

    List<Seat> findByStatusAndLockExpiresAtBefore(SeatStatus status, LocalDateTime dateTime);

    @Modifying
    @Query("UPDATE Seat s SET s.status = 'AVAILABLE', s.lockExpiresAt = NULL, s.lockedByCartId = NULL " +
           "WHERE s.trip.id = :tripId AND s.status = 'LOCKED' AND s.lockExpiresAt < :now")
    int releaseExpiredLocksForTrip(@Param("tripId") Long tripId, @Param("now") LocalDateTime now);
}
