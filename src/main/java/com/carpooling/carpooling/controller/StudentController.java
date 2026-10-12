package com.carpooling.carpooling.controller;

import com.carpooling.carpooling.model.Student;
import com.carpooling.carpooling.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(
    origins = {"http://localhost:5173", "http://localhost:3000"},
    allowedHeaders = "*",
    methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE,
        RequestMethod.OPTIONS
    }
)
public class StudentController {

    private final StudentRepository students;
    private final PasswordEncoder encoder;

    public StudentController(
            StudentRepository students,
            PasswordEncoder encoder) {
        this.students = students;
        this.encoder = encoder;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody Student student) {

        String email = student.getEmail().trim().toLowerCase();

        if (students.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "Email is already registered"
                    ));
        }

        student.setEmail(email);
        student.setPassword(
                encoder.encode(student.getPassword())
        );

        Student saved = students.save(student);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", saved.getId(),
                        "name", saved.getName(),
                        "email", saved.getEmail(),
                        "college", saved.getCollege(),
                        "phone", saved.getPhone(),
                        "message", "Student registered successfully"
                ));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error",
                            "Email and password are required"
                    ));
        }

        Student student = students
                .findByEmailIgnoreCase(email.trim())
                .orElse(null);

        if (student == null
                || !encoder.matches(
                        password,
                        student.getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error",
                            "Invalid email or password"
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "id", student.getId(),
                        "studentId", student.getId(),
                        "name", student.getName(),
                        "email", student.getEmail(),
                        "college", student.getCollege(),
                        "phone", student.getPhone(),
                        "message", "Login successful"
                )
        );
    }

    // GET ALL STUDENTS
    @GetMapping
    public List<Map<String, Object>> listStudents() {

        return students.findAll()
                .stream()
                .map(s -> Map.<String, Object>of(
                        "id", s.getId(),
                        "name", s.getName(),
                        "email", s.getEmail(),
                        "college", s.getCollege(),
                        "phone", s.getPhone()
                ))
                .toList();
    }

    // GET STUDENT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudent(
            @PathVariable Long id) {

        return students.findById(id)
                .<ResponseEntity<?>>map(s ->
                        ResponseEntity.ok(
                                Map.of(
                                        "id", s.getId(),
                                        "name", s.getName(),
                                        "email", s.getEmail(),
                                        "college", s.getCollege(),
                                        "phone", s.getPhone()
                                )
                        )
                )
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                        "error",
                                        "Student not found"
                                ))
                );
    }
}