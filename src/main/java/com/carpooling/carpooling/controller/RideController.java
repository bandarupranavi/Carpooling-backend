package com.carpooling.carpooling.controller;

import com.carpooling.carpooling.model.Ride;
import com.carpooling.carpooling.model.Student;
import com.carpooling.carpooling.repository.RideRepository;
import com.carpooling.carpooling.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class RideController {
    private final RideRepository rides;
    private final StudentRepository students;

    public RideController(RideRepository rides, StudentRepository students) {
        this.rides = rides; this.students = students;
    }

    @PostMapping
    public ResponseEntity<?> createRide(@Valid @RequestBody RideRequest request) {
        Student driver = students.findById(request.driverId())
            .orElse(null);
        if (driver == null) return ResponseEntity.badRequest().body(Map.of("error", "Driver studentId does not exist"));
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setOrigin(request.origin().trim());
        ride.setDestination(request.destination().trim());
        ride.setDepartureTime(request.departureTime());
        ride.setSeatsAvailable(request.seatsAvailable());
        ride.setPricePerSeat(request.pricePerSeat());
        return ResponseEntity.status(HttpStatus.CREATED).body(rides.save(ride));
    }

    @GetMapping
    public List<Ride> searchRides(
        @RequestParam(defaultValue = "") String origin,
        @RequestParam(defaultValue = "") String destination) {
        return rides.findByOriginContainingIgnoreCaseAndDestinationContainingIgnoreCaseAndDepartureTimeAfterAndSeatsAvailableGreaterThan(
            origin, destination, LocalDateTime.now(), 0);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRide(@PathVariable Long id) {
        return rides.findById(id).<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Ride not found")));
    }

    @GetMapping("/driver/{driverId}")
    public List<Ride> driverRides(@PathVariable Long driverId) {
        return rides.findByDriverId(driverId);
    }

    public record RideRequest(
        @jakarta.validation.constraints.NotNull Long driverId,
        @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=150) String origin,
        @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=150) String destination,
        @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.Future LocalDateTime departureTime,
        @jakarta.validation.constraints.Min(1) @jakarta.validation.constraints.Max(8) int seatsAvailable,
        @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.DecimalMin("0.00") java.math.BigDecimal pricePerSeat
    ) {}
}
