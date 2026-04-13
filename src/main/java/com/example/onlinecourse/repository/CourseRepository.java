package com.example.onlinecourse.repository;

import com.example.onlinecourse.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findBySeatsRemainingGreaterThan(int seatsRemaining);

    List<Course> findByTitleContainingIgnoreCase(String keyword);
}
