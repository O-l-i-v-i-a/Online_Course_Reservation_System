package com.example.onlinecourse.service;

import com.example.onlinecourse.domain.Course;
import com.example.onlinecourse.domain.Enrollment;
import com.example.onlinecourse.domain.ReservationStatus;
import com.example.onlinecourse.domain.Student;
import com.example.onlinecourse.dto.EnrollmentDTO;
import com.example.onlinecourse.dto.EnrollmentInsightsDTO;
import com.example.onlinecourse.exception.BusinessException;
import com.example.onlinecourse.exception.ResourceNotFoundException;
import com.example.onlinecourse.repository.EnrollmentRepository;
import com.example.onlinecourse.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class EnrollmentService {

    private final CourseService courseService;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(CourseService courseService,
                             StudentRepository studentRepository,
                             EnrollmentRepository enrollmentRepository) {
        this.courseService = courseService;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findByStudent(Integer studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Enrollment findById(Integer id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id " + id));
    }

    @Transactional
    public Enrollment enroll(EnrollmentDTO dto) {
        Integer studentId = Objects.requireNonNull(dto.studentId(), "Student id must not be null");
        Integer courseId = Objects.requireNonNull(dto.courseId(), "Course id must not be null");

        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + studentId));

        Course course = courseService.findById(courseId);
        boolean alreadyEnrolled = enrollmentRepository.existsActiveReservationForCourse(
            studentId,
            courseId,
            List.of(ReservationStatus.pending, ReservationStatus.confirmed)
        );
        if (alreadyEnrolled) {
            throw new BusinessException("Student is already enrolled in this course");
        }

        if (!course.takeSeat()) {
            throw new BusinessException("Course has no available seats");
        }
        courseService.updateAvailableSeatsAfterReservation(course);

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setStatus(ReservationStatus.confirmed);
        enrollment.setStudent(student);
        enrollment.setCourses(Set.of(course));
        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public Enrollment drop(Integer enrollmentId) {
        Integer reservationId = Objects.requireNonNull(enrollmentId, "Enrollment id must not be null");
        Enrollment enrollment = enrollmentRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id " + enrollmentId));

        if (!enrollment.drop()) {
            throw new BusinessException("Unable to drop this course");
        }
        enrollment.getCourses().forEach(courseService::updateAvailableSeatsAfterReservation);
        return enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public EnrollmentInsightsDTO insights() {
        long totalStudents = studentRepository.count();
        long totalCourses = courseService.findAll().size();
        long totalEnrollments = enrollmentRepository.count();
        List<Enrollment> allReservations = enrollmentRepository.findAll();
        long activeEnrollments = allReservations.stream()
            .filter(e -> e.getStatus() == ReservationStatus.confirmed || e.getStatus() == ReservationStatus.pending)
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
}
