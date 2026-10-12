package com.carpooling.carpooling.repository;

import com.carpooling.carpooling.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<Student> findByEmailIgnoreCase(String email);
}
