package com.example.onlinecourse.service;

import com.example.onlinecourse.domain.Course;
import com.example.onlinecourse.domain.Enrollment;
import com.example.onlinecourse.domain.Student;
import com.example.onlinecourse.dto.EnrollmentDTO;
import com.example.onlinecourse.dto.EnrollmentInsightsDTO;
import com.example.onlinecourse.exception.BusinessException;
import com.example.onlinecourse.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EnrollmentService {

    private final CourseService courseService;
    private final Map<Integer, Student> students = new ConcurrentHashMap<>();
    private final Map<Integer, Enrollment> enrollments = new ConcurrentHashMap<>();
    private final AtomicInteger studentIdSequence = new AtomicInteger(1);
    private final AtomicInteger enrollmentIdSequence = new AtomicInteger(1);

    public EnrollmentService(CourseService courseService) {
        this.courseService = courseService;
        seedStudents();
    }

    public List<Enrollment> findByStudent(Integer studentId) {
        return enrollments.values().stream()
                .filter(e -> e.getStudent().getId().equals(studentId))
                .toList();
    }

    public List<Enrollment> findAll() {
        return new ArrayList<>(enrollments.values());
    }

    public Enrollment enroll(EnrollmentDTO dto) {
        Student student = students.get(dto.studentId());
        if (student == null) {
            throw new ResourceNotFoundException("Student not found with id " + dto.studentId());
        }

        Course course = courseService.findById(dto.courseId());
        boolean alreadyEnrolled = enrollments.values().stream()
                .anyMatch(e -> e.getStudent().getId().equals(dto.studentId())
                        && e.getCourse().getId().equals(dto.courseId())
                        && !"DROPPED".equals(e.getStatus()));
        if (alreadyEnrolled) {
            throw new BusinessException("Student is already enrolled in this course");
        }

        if (!course.takeSeat()) {
            throw new BusinessException("Course has no available seats");
        }

        int enrollmentId = enrollmentIdSequence.getAndIncrement();
        Enrollment enrollment = new Enrollment(enrollmentId, student, course);
        enrollments.put(enrollmentId, enrollment);
        return enrollment;
    }

    public Enrollment drop(Integer enrollmentId) {
        Enrollment enrollment = enrollments.get(enrollmentId);
        if (enrollment == null) {
            throw new ResourceNotFoundException("Enrollment not found with id " + enrollmentId);
        }
        if (!enrollment.drop()) {
            throw new BusinessException("Unable to drop this course");
        }
        return enrollment;
    }

    public EnrollmentInsightsDTO insights() {
        long totalStudents = students.size();
        long totalCourses = courseService.findAll().size();
        long totalEnrollments = enrollments.size();
        long activeEnrollments = enrollments.values().stream()
                .filter(e -> "ACTIVE".equals(e.getStatus()))
                .count();
        long droppedEnrollments = totalEnrollments - activeEnrollments;
        long fullyBookedCourses = courseService.findAll().stream()
                .filter(c -> c.getSeatsRemaining() == 0)
                .count();

        return new EnrollmentInsightsDTO(
                totalStudents,
                totalCourses,
                totalEnrollments,
                activeEnrollments,
                droppedEnrollments,
                fullyBookedCourses
        );
    }

    private void seedStudents() {
        int id1 = studentIdSequence.getAndIncrement();
        students.put(id1, new Student(id1, "Ada Lovelace", "ada@example.com"));
        int id2 = studentIdSequence.getAndIncrement();
        students.put(id2, new Student(id2, "Alan Turing", "alan@example.com"));
    }
}
