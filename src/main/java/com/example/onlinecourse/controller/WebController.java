package com.example.onlinecourse.controller;

import com.example.onlinecourse.domain.Enrollment;
import com.example.onlinecourse.dto.CourseDTO;
import com.example.onlinecourse.dto.EnrollmentDTO;
import com.example.onlinecourse.exception.BusinessException;
import com.example.onlinecourse.service.CourseService;
import com.example.onlinecourse.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class WebController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping({"/", ""})
    public String landing() {
        return "landing";
    }

    @PostMapping("/ui/courses")
    public String createCourse(@RequestParam Integer instructorId,
                               @RequestParam String title,
                               @RequestParam(required = false) String description,
                               @RequestParam Integer totalSeats,
                               @RequestParam(defaultValue = "0") BigDecimal price,
                               RedirectAttributes redirectAttributes) {
        courseService.createCourse(new CourseDTO(null, instructorId, title, description, null, totalSeats, price));
        redirectAttributes.addFlashAttribute("message", "Course created successfully.");
        return "redirect:/dashboard";
    }

    @PostMapping("/ui/enrollments")
    public String createReservation(@RequestParam Integer studentId,
                                    @RequestParam Integer courseId,
                                    RedirectAttributes redirectAttributes) {
        Enrollment enrollment = enrollmentService.enroll(new EnrollmentDTO(studentId, courseId));
        redirectAttributes.addFlashAttribute("message", "Reservation created with ID " + enrollment.getId());
        return "redirect:/dashboard";
    }

    @PostMapping("/ui/enrollments/{id}/cancel")
    public String cancelReservation(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.drop(id);
            redirectAttributes.addFlashAttribute("message", "Reservation " + id + " cancelled.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/dashboard";
    }
}
