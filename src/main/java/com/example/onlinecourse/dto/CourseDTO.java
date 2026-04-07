package com.example.onlinecourse.dto;

/**
 * Payload for creating or updating courses via the REST API.
 */
public record CourseDTO(
        Integer id,
        String title,
        String description,
        int capacity
) {}
