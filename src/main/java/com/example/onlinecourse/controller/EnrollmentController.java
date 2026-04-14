package com.example.onlinecourse.controller;

import com.example.onlinecourse.domain.Enrollment;
import com.example.onlinecourse.domain.AppUser;
import com.example.onlinecourse.domain.UserRole;
import com.example.onlinecourse.dto.ApiResponse;
import com.example.onlinecourse.dto.EnrollmentDTO;
import com.example.onlinecourse.dto.EnrollmentInsightsDTO;
import com.example.onlinecourse.service.EnrollmentService;
import com.example.onlinecourse.service.UserService;
import com.example.onlinecourse.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public List<Enrollment> all() {
        return enrollmentService.findAll();
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR','ADMIN')")
    public List<Enrollment> byStudent(@PathVariable Integer studentId, Authentication authentication) {
        // STUDENT can only view their own enrollments
        if (authentication != null) {
            AppUser user = appUserRepository.findByEmail(authentication.getName()).orElse(null);
            if (user != null && user.getRole() == UserRole.student) {
                var student = userService.ensureStudentProfile(user);
                if (student != null && !student.getId().equals(studentId)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own enrollments");
                }
            }
        }
        return enrollmentService.findByStudent(studentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Enrollment> enroll(@RequestBody EnrollmentDTO dto, Authentication authentication) {
        // Verify the student is enrolling for themselves
        AppUser user = appUserRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        
        var student = userService.ensureStudentProfile(user);
        
        if (!student.getId().equals(dto.studentId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only enroll yourself");
        }
        
        Enrollment enrollment = enrollmentService.enroll(dto);
        return ApiResponse.success("Enrollment created", enrollment);
    }

    @PostMapping("/{enrollmentId}/drop")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Enrollment> drop(@PathVariable Integer enrollmentId, Authentication authentication) {
        // Verify the student owns this enrollment
        Enrollment enrollment = enrollmentService.findById(enrollmentId);
        AppUser user = appUserRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        
        var student = userService.ensureStudentProfile(user);
        
        if (!enrollment.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only cancel your own enrollments");
        }
        
        Enrollment dropped = enrollmentService.drop(enrollmentId);
        return ApiResponse.success("Enrollment dropped", dropped);
    }

    @GetMapping("/insights")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<EnrollmentInsightsDTO> insights() {
        return ApiResponse.success("Platform insights", enrollmentService.insights());
    }
}
