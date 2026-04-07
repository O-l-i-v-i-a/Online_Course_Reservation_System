package com.example.onlinecourse.dto;

/**
 * Payload for enrolling a student into a course.
 */
public record EnrollmentDTO(
        Integer studentId,
        Integer courseId
) {}
