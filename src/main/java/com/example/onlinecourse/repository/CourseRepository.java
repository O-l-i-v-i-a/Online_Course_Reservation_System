package com.example.onlinecourse.repository;

import com.example.onlinecourse.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("SELECT c FROM Course c WHERE c.availableSeats > 0")
    List<Course> findAvailableCourses();

    List<Course> findByTitleContainingIgnoreCase(String keyword);
}
