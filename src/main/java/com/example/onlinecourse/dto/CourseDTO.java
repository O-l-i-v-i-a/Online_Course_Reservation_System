package com.example.onlinecourse.dto;

import java.math.BigDecimal;

/**
 * Payload for creating or updating courses via the REST API.
 */
public record CourseDTO(
        Integer id,
        Integer instructorId,
        String title,
        String description,
        Integer capacity,
        Integer totalSeats,
        BigDecimal price
) {}
