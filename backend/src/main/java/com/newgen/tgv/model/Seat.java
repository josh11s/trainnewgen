package com.newgen.tgv.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(name = "uk_trip_coach_seat", columnNames = {"trip_id", "coach_number", "seat_number"})
}, indexes = {
    @Index(name = "idx_seats_trip", columnList = "trip_id"),
    @Index(name = "idx_seats_trip_status", columnList = "trip_id, status")
})
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "coach_number", nullable = false)
    private Integer coachNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "coach_class", nullable = false, length = 20)
    private CoachClass coachClass;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "seat_code", nullable = false, length = 20)
    private String seatCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatPosition position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatStatus status;

    @Column(name = "lock_expires_at")
    private LocalDateTime lockExpiresAt;

    @Column(name = "locked_by_cart_id")
    private UUID lockedByCartId;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    public Seat() {
    }

    public Seat(Trip trip, Integer coachNumber, CoachClass coachClass, Integer seatNumber,
                String seatCode, SeatPosition position, SeatStatus status) {
        this.trip = trip;
        this.coachNumber = coachNumber;
        this.coachClass = coachClass;
        this.seatNumber = seatNumber;
        this.seatCode = seatCode;
        this.position = position;
        this.status = status;
        this.version = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Integer getCoachNumber() {
        return coachNumber;
    }

    public void setCoachNumber(Integer coachNumber) {
        this.coachNumber = coachNumber;
    }

    public CoachClass getCoachClass() {
        return coachClass;
    }

    public void setCoachClass(CoachClass coachClass) {
        this.coachClass = coachClass;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public SeatPosition getPosition() {
        return position;
    }

    public void setPosition(SeatPosition position) {
        this.position = position;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public LocalDateTime getLockExpiresAt() {
        return lockExpiresAt;
    }

    public void setLockExpiresAt(LocalDateTime lockExpiresAt) {
        this.lockExpiresAt = lockExpiresAt;
    }

    public UUID getLockedByCartId() {
        return lockedByCartId;
    }

    public void setLockedByCartId(UUID lockedByCartId) {
        this.lockedByCartId = lockedByCartId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
