package com.carpooling.carpooling.repository;

import com.carpooling.carpooling.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStudentId(Long studentId);
    List<Booking> findByRideId(Long rideId);
}
