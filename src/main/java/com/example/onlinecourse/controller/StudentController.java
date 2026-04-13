package com.example.onlinecourse.controller;

import com.example.onlinecourse.domain.AppUser;
import com.example.onlinecourse.domain.Student;
import com.example.onlinecourse.dto.StudentMeDTO;
import com.example.onlinecourse.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final UserService userService;

    public StudentController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentMeDTO me(Authentication authentication) {
        AppUser user = userService.findByEmail(authentication.getName());
        Student student = userService.ensureStudentProfile(user);
        return new StudentMeDTO(student.getId(), user.getName(), user.getEmail(), student.getPhone());
    }
}
