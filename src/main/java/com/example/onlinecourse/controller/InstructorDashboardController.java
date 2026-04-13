package com.example.onlinecourse.controller;

import com.example.onlinecourse.domain.AppUser;
import com.example.onlinecourse.domain.Instructor;
import com.example.onlinecourse.service.CourseService;
import com.example.onlinecourse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/instructor")
@PreAuthorize("hasRole('INSTRUCTOR')")
public class InstructorDashboardController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        AppUser user = userService.findByEmail(authentication.getName());
        Instructor instructor = userService.ensureInstructorProfile(user);

        // Get only courses taught by this instructor
        var courses = courseService.findAll().stream()
                .filter(c -> c.getInstructor() != null && c.getInstructor().getId().equals(instructor.getId()))
                .toList();

        model.addAttribute("courses", courses);
        model.addAttribute("instructor", instructor);
        model.addAttribute("instructorId", instructor.getId());
        return "instructor-dashboard";
    }
}
