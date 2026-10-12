package com.carpooling.carpooling.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
public class Ride {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Student driver;

    @NotBlank @Size(max = 150)
    @Column(nullable = false)
    private String origin;

    @NotBlank @Size(max = 150)
    @Column(nullable = false)
    private String destination;

    @NotNull @Future
    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Min(1) @Max(8)
    @Column(nullable = false)
    private int seatsAvailable;

    @NotNull @DecimalMin("0.00") @Digits(integer = 7, fraction = 2)
    @Column(nullable = false, precision = 9, scale = 2)
    private BigDecimal pricePerSeat;

    public Ride() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getDriver() { return driver; }
    public void setDriver(Student driver) { this.driver = driver; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    public int getSeatsAvailable() { return seatsAvailable; }
    public void setSeatsAvailable(int seatsAvailable) { this.seatsAvailable = seatsAvailable; }
    public BigDecimal getPricePerSeat() { return pricePerSeat; }
    public void setPricePerSeat(BigDecimal pricePerSeat) { this.pricePerSeat = pricePerSeat; }
}
