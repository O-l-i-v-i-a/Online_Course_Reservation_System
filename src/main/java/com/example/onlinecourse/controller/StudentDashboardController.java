package com.example.onlinecourse.controller;

import com.example.onlinecourse.domain.AppUser;
import com.example.onlinecourse.domain.Student;
import com.example.onlinecourse.service.CourseService;
import com.example.onlinecourse.service.EnrollmentService;
import com.example.onlinecourse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentDashboardController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        AppUser user = userService.findByEmail(authentication.getName());
        Student student = userService.ensureStudentProfile(user);

        // Get available courses
        var availableCourses = courseService.findAvailable();

        // Get student's reservations
        var reservations = enrollmentService.findByStudent(student.getId());

        model.addAttribute("courses", availableCourses);
        model.addAttribute("reservations", reservations);
        model.addAttribute("student", student);
        model.addAttribute("studentId", student.getId());
        return "student-dashboard";
    }
}
