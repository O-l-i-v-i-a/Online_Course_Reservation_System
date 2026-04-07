package com.example.onlinecourse.controller;

import com.example.onlinecourse.domain.Course;
import com.example.onlinecourse.dto.CourseDTO;
import com.example.onlinecourse.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

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
    public Course create(@RequestBody CourseDTO dto) {
        return courseService.createCourse(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public Course update(@PathVariable Integer id, @RequestBody CourseDTO dto) {
        return courseService.updateCourse(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public void delete(@PathVariable Integer id) {
        courseService.deleteCourse(id);
    }
}
