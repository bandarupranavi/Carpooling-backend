package com.carpooling.carpooling.repository;

import com.carpooling.carpooling.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByOriginContainingIgnoreCaseAndDestinationContainingIgnoreCaseAndDepartureTimeAfterAndSeatsAvailableGreaterThan(
        String origin, String destination, LocalDateTime now, int seats);
    List<Ride> findByDriverId(Long driverId);
}
