package com.example.onlinecourse.controller;

import com.example.onlinecourse.domain.Course;
import com.example.onlinecourse.domain.UserRole;
import com.example.onlinecourse.dto.CourseDTO;
import com.example.onlinecourse.repository.AppUserRepository;
import com.example.onlinecourse.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private AppUserRepository appUserRepository;

    @GetMapping
    public List<Course> getAll() {
        return courseService.findAll();
    }

    @GetMapping("/available")
    public List<Course> available() {
        return courseService.findAvailable();
    }

    @GetMapping("/{id}")
    public Course getById(@PathVariable Integer id) {
        return courseService.findById(id);
    }

    @GetMapping("/search")
    public List<Course> search(@RequestParam String keyword) {
        return courseService.search(keyword);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public Course create(@RequestBody CourseDTO dto, Authentication authentication) {
        return courseService.createCourse(dto, authentication);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public Course update(@PathVariable Integer id, @RequestBody CourseDTO dto, Authentication authentication) {
        return courseService.updateCourse(id, dto, authentication);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public void delete(@PathVariable Integer id, Authentication authentication) {
        courseService.deleteCourse(id, authentication);
    }
}
