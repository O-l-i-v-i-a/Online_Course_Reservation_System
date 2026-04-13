package com.example.onlinecourse.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/api")
    public Map<String, String> hello() {
        return Map.of(
                "message", "Online Course Reservation API",
                "courses", "/api/courses",
                "enrollments", "/api/enrollments"
        );
    }
}
