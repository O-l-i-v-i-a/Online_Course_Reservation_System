package com.example.onlinecourse.domain;

import java.time.LocalDate;

public class Enrollment {
    private Integer id;
    private String status; // ACTIVE or DROPPED
    private LocalDate enrollmentDate;
    private Student student;
    private Course course;

    public Enrollment() {}

    public Enrollment(Integer id, Student student, Course course) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.enrollmentDate = LocalDate.now();
        this.status = "ACTIVE";
    }

    public Integer getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public boolean drop() {
        if ("DROPPED".equals(status)) {
            return false;
        }
        status = "DROPPED";
        course.releaseSeat();
        return true;
    }
}
