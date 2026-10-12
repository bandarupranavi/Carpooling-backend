package com.carpooling.carpooling.controller;

import com.carpooling.carpooling.model.Booking;
import com.carpooling.carpooling.model.Ride;
import com.carpooling.carpooling.model.Student;
import com.carpooling.carpooling.repository.BookingRepository;
import com.carpooling.carpooling.repository.RideRepository;
import com.carpooling.carpooling.repository.StudentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@Validated
public class BookingController {
    private final BookingRepository bookings;
    private final RideRepository rides;
    private final StudentRepository students;

    public BookingController(BookingRepository bookings, RideRepository rides, StudentRepository students) {
        this.bookings = bookings; this.rides = rides; this.students = students;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> bookRide(@Valid @RequestBody BookingRequest request) {
        Ride ride = rides.findById(request.rideId()).orElse(null);
        if (ride == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Ride not found"));
        Student student = students.findById(request.studentId()).orElse(null);
        if (student == null) return ResponseEntity.badRequest().body(Map.of("error", "Student not found"));
        if (ride.getDriver().getId().equals(student.getId()))
            return ResponseEntity.badRequest().body(Map.of("error", "A driver cannot book their own ride"));
        if (ride.getSeatsAvailable() < request.seatsBooked())
            return ResponseEntity.badRequest().body(Map.of("error", "Not enough seats available"));

        ride.setSeatsAvailable(ride.getSeatsAvailable() - request.seatsBooked());
        rides.save(ride);
        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setStudent(student);
        booking.setSeatsBooked(request.seatsBooked());
        booking.setStatus("CONFIRMED");
        return ResponseEntity.status(HttpStatus.CREATED).body(bookings.save(booking));
    }

    @GetMapping("/student/{studentId}")
    public List<Booking> studentBookings(@PathVariable Long studentId) {
        return bookings.findByStudentId(studentId);
    }

    @GetMapping("/ride/{rideId}")
    public List<Booking> rideBookings(@PathVariable Long rideId) {
        return bookings.findByRideId(rideId);
    }

    @DeleteMapping("/{bookingId}")
    @Transactional
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        Booking booking = bookings.findById(bookingId).orElse(null);
        if (booking == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Booking not found"));
        if ("CANCELLED".equals(booking.getStatus()))
            return ResponseEntity.badRequest().body(Map.of("error", "Booking is already cancelled"));
        Ride ride = booking.getRide();
        ride.setSeatsAvailable(ride.getSeatsAvailable() + booking.getSeatsBooked());
        rides.save(ride);
        booking.setStatus("CANCELLED");
        bookings.save(booking);
        return ResponseEntity.ok(Map.of("message", "Booking cancelled successfully"));
    }

    public record BookingRequest(@NotNull Long rideId, @NotNull Long studentId, @Min(1) int seatsBooked) {}
}
