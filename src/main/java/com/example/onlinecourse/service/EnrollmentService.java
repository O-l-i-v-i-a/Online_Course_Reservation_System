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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class EnrollmentService {

    private final CourseService courseService;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final JdbcTemplate jdbcTemplate;

    public EnrollmentService(CourseService courseService,
                             StudentRepository studentRepository,
                             EnrollmentRepository enrollmentRepository,
                             JdbcTemplate jdbcTemplate) {
        this.courseService = courseService;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.jdbcTemplate = jdbcTemplate;
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

        LocalDate today = LocalDate.now();
        String status = ReservationStatus.pending.name();

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                """
                INSERT INTO RESERVATION (`ReservationDate`, reservation_date, `Status`, `StudentID`, student_id)
                VALUES (?, ?, ?, ?, ?)
                """,
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setObject(1, today);
            ps.setObject(2, today);
            ps.setString(3, status);
            ps.setInt(4, studentId);
            ps.setInt(5, studentId);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new BusinessException("Failed to create reservation");
        }

        int reservationId = key.intValue();
        jdbcTemplate.update(
            """
            INSERT INTO RESERVATION_COURSE (`ReservationID`, reservation_id, `CourseID`, course_id)
            VALUES (?, ?, ?, ?)
            """,
            reservationId,
            reservationId,
            courseId,
            courseId
        );

        return enrollmentRepository.findById(reservationId)
            .orElseThrow(() -> new BusinessException("Reservation created but could not be loaded"));
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
