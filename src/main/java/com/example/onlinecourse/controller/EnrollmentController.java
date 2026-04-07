package com.example.onlinecourse.controller;

import com.example.onlinecourse.domain.Enrollment;
import com.example.onlinecourse.dto.ApiResponse;
import com.example.onlinecourse.dto.EnrollmentDTO;
import com.example.onlinecourse.dto.EnrollmentInsightsDTO;
import com.example.onlinecourse.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public List<Enrollment> all() {
        return enrollmentService.findAll();
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR','ADMIN')")
    public List<Enrollment> byStudent(@PathVariable Integer studentId) {
        return enrollmentService.findByStudent(studentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Enrollment> enroll(@RequestBody EnrollmentDTO dto) {
        Enrollment enrollment = enrollmentService.enroll(dto);
        return ApiResponse.success("Enrollment created", enrollment);
    }

    @PostMapping("/{enrollmentId}/drop")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Enrollment> drop(@PathVariable Integer enrollmentId) {
        Enrollment enrollment = enrollmentService.drop(enrollmentId);
        return ApiResponse.success("Enrollment dropped", enrollment);
    }

    @GetMapping("/insights")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<EnrollmentInsightsDTO> insights() {
        return ApiResponse.success("Platform insights", enrollmentService.insights());
    }
}
