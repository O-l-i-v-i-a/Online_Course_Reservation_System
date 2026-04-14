package com.example.onlinecourse.controller;

import com.example.onlinecourse.domain.UserRole;
import com.example.onlinecourse.service.CourseService;
import com.example.onlinecourse.service.EnrollmentService;
import com.example.onlinecourse.service.UserService;
import com.example.onlinecourse.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("reservations", enrollmentService.findAll());
        model.addAttribute("students", enrollmentService.findAllStudents());
        model.addAttribute("instructors", courseService.findAllInstructors());
        model.addAttribute("users", appUserRepository.findAll());
        return "admin-dashboard";
    }

    @PostMapping("/users")
    public String createUser(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String role,
                             @RequestParam(required = false) String phone,
                             @RequestParam(required = false) String specialization,
                             RedirectAttributes redirectAttributes) {
        try {
            var user = userService.createUser(name, email, password, role, phone, specialization);
            String roleLabel = user.getRole().name().toUpperCase();
            redirectAttributes.addFlashAttribute("successMessage",
                    "Created " + roleLabel + " user: " + user.getEmail());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
