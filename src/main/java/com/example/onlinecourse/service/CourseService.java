package com.example.onlinecourse.service;

import com.example.onlinecourse.domain.Course;
import com.example.onlinecourse.dto.CourseDTO;
import com.example.onlinecourse.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final Map<Integer, Course> courses = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);

    public CourseService() {
        // seed a couple of courses to make testing easier
        createCourse(new CourseDTO(null, "Spring Boot Basics", "Intro to Spring Boot", 30));
        createCourse(new CourseDTO(null, "REST APIs", "Build clean RESTful APIs", 25));
    }

    public List<Course> findAll() {
        return new ArrayList<>(courses.values());
    }

    public List<Course> findAvailable() {
        return courses.values().stream()
                .filter(c -> c.getSeatsRemaining() > 0)
                .collect(Collectors.toList());
    }

    public Course findById(Integer id) {
        Course course = courses.get(id);
        if (course == null) {
            throw new ResourceNotFoundException("Course not found with id " + id);
        }
        return course;
    }

    public List<Course> search(String keyword) {
        return courses.values().stream()
                .filter(c -> c.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Course createCourse(CourseDTO dto) {
        int id = idSequence.getAndIncrement();
        Course course = new Course(id, dto.title(), dto.description(), dto.capacity());
        courses.put(id, course);
        return course;
    }

    public Course updateCourse(Integer id, CourseDTO dto) {
        Course course = findById(id);
        course.setTitle(dto.title());
        course.setDescription(dto.description());
        course.setCapacity(dto.capacity());
        return course;
    }

    public void deleteCourse(Integer id) {
        if (courses.remove(id) == null) {
            throw new ResourceNotFoundException("Course not found with id " + id);
        }
    }
}
