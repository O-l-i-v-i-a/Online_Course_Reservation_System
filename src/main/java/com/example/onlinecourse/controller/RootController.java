package com.example.onlinecourse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class RootController {

    @GetMapping({"/", ""})
    public String home() {
        return "forward:/index.html";
    }

    @GetMapping("/api")
    @ResponseBody
    public Map<String, String> hello() {
        return Map.of(
                "message", "Online Course Reservation API",
                "courses", "/api/courses",
                "enrollments", "/api/enrollments"
        );
    }
}
