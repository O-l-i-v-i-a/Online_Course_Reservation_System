package com.example.onlinecourse.dto;

public class EnrollmentInsightsDTO {
    private final long totalStudents;
    private final long totalCourses;
    private final long totalEnrollments;
    private final long activeEnrollments;
    private final long droppedEnrollments;
    private final long fullyBookedCourses;

    public EnrollmentInsightsDTO(long totalStudents,
                                 long totalCourses,
                                 long totalEnrollments,
                                 long activeEnrollments,
                                 long droppedEnrollments,
                                 long fullyBookedCourses) {
        this.totalStudents = totalStudents;
        this.totalCourses = totalCourses;
        this.totalEnrollments = totalEnrollments;
        this.activeEnrollments = activeEnrollments;
        this.droppedEnrollments = droppedEnrollments;
        this.fullyBookedCourses = fullyBookedCourses;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public long getTotalCourses() {
        return totalCourses;
    }

    public long getTotalEnrollments() {
        return totalEnrollments;
    }

    public long getActiveEnrollments() {
        return activeEnrollments;
    }

    public long getDroppedEnrollments() {
        return droppedEnrollments;
    }

    public long getFullyBookedCourses() {
        return fullyBookedCourses;
    }
}
