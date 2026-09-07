package com.newgen.tgv.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips", uniqueConstraints = {
    @UniqueConstraint(name = "uk_trip_train_time", columnNames = {"trainNumber", "departureTime"})
})
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String trainNumber;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "departure_station_id", nullable = false)
    private Station departureStation;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "arrival_station_id", nullable = false)
    private Station arrivalStation;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "first_class_price", precision = 10, scale = 2)
    private BigDecimal firstClassPrice;

    @Column(nullable = false)
    private Integer standardSeatsAvailable;

    @Column(nullable = false)
    private Integer firstSeatsAvailable;

    public Trip() {
    }

    public Trip(String trainNumber, Station departureStation, Station arrivalStation,
                LocalDateTime departureTime, LocalDateTime arrivalTime, Integer durationMinutes,
                BigDecimal basePrice, Integer standardSeatsAvailable, Integer firstSeatsAvailable) {
        this(trainNumber, departureStation, arrivalStation, departureTime, arrivalTime, durationMinutes,
                basePrice, basePrice != null ? basePrice.multiply(java.math.BigDecimal.valueOf(1.5)).setScale(2, java.math.RoundingMode.HALF_UP) : null,
                standardSeatsAvailable, firstSeatsAvailable);
    }

    public Trip(String trainNumber, Station departureStation, Station arrivalStation,
                LocalDateTime departureTime, LocalDateTime arrivalTime, Integer durationMinutes,
                BigDecimal basePrice, BigDecimal firstClassPrice, Integer standardSeatsAvailable, Integer firstSeatsAvailable) {
        this.trainNumber = trainNumber;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.durationMinutes = durationMinutes;
        this.basePrice = basePrice;
        this.firstClassPrice = firstClassPrice;
        this.standardSeatsAvailable = standardSeatsAvailable;
        this.firstSeatsAvailable = firstSeatsAvailable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public Station getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(Station departureStation) {
        this.departureStation = departureStation;
    }

    public Station getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(Station arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getFirstClassPrice() {
        if (firstClassPrice != null) {
            return firstClassPrice;
        }
        return basePrice != null ? basePrice.multiply(java.math.BigDecimal.valueOf(1.5)).setScale(2, java.math.RoundingMode.HALF_UP) : null;
    }

    public void setFirstClassPrice(BigDecimal firstClassPrice) {
        this.firstClassPrice = firstClassPrice;
    }

    public Integer getStandardSeatsAvailable() {
        return standardSeatsAvailable;
    }

    public void setStandardSeatsAvailable(Integer standardSeatsAvailable) {
        this.standardSeatsAvailable = standardSeatsAvailable;
    }

    public Integer getFirstSeatsAvailable() {
        return firstSeatsAvailable;
    }

    public void setFirstSeatsAvailable(Integer firstSeatsAvailable) {
        this.firstSeatsAvailable = firstSeatsAvailable;
    }
}
