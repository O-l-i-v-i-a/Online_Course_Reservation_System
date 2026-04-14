package com.example.onlinecourse.service;

import com.example.onlinecourse.domain.Course;
import com.example.onlinecourse.domain.Instructor;
import com.example.onlinecourse.domain.AppUser;
import com.example.onlinecourse.domain.UserRole;
import com.example.onlinecourse.dto.CourseDTO;
import com.example.onlinecourse.exception.BusinessException;
import com.example.onlinecourse.exception.ResourceNotFoundException;
import com.example.onlinecourse.repository.CourseRepository;
import com.example.onlinecourse.repository.InstructorRepository;
import com.example.onlinecourse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private UserService userService;

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Instructor> findAllInstructors() {
        return instructorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Course> findAvailable() {
        return courseRepository.findAvailableCourses();
    }

    @Transactional(readOnly = true)
    public Course findById(Integer id) {
        Integer courseId = Objects.requireNonNull(id, "Course id must not be null");
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + id));
    }

    @Transactional(readOnly = true)
    public List<Course> search(String keyword) {
        return courseRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Transactional
    public Course createCourse(CourseDTO dto) {
        return createCourse(dto, null);
    }

    @Transactional
    public Course createCourse(CourseDTO dto, Authentication authentication) {
        // Auto-assign instructor ID if user is INSTRUCTOR and not provided
        Integer instructorId = dto.instructorId();
        
        if (instructorId == null && authentication != null) {
            // User is INSTRUCTOR, auto-assign their ID
            AppUser user = userService.findByEmail(authentication.getName());
            if (user.getRole() == UserRole.instructor) {
                Instructor instructor = userService.ensureInstructorProfile(user);
                instructorId = instructor.getId();
            }
        }
        
        final Integer finalInstructorId = instructorId;
        if (finalInstructorId == null) {
            throw new ResourceNotFoundException("Instructor id is required");
        }

        Instructor instructor = instructorRepository.findById(finalInstructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id " + finalInstructorId));

        int seats = resolveTotalSeats(dto);
        BigDecimal price = dto.price() == null ? BigDecimal.ZERO : dto.price();

        Course course = new Course();
        course.setInstructor(instructor);
        course.setTitle(dto.title());
        course.setDescription(dto.description());
        course.setPrice(price);
        course.setCapacity(seats);
        course.setSeatsRemaining(seats);

        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Integer id, CourseDTO dto) {
        return updateCourse(id, dto, null);
    }

    @Transactional
    public Course updateCourse(Integer id, CourseDTO dto, Authentication authentication) {
        Integer courseId = Objects.requireNonNull(id, "Course id must not be null");
        Course course = findById(courseId);
        
        // Check authorization: INSTRUCTOR can only edit their own courses
        if (authentication != null) {
            AppUser user = userService.findByEmail(authentication.getName());
            if (user != null && user.getRole() == UserRole.instructor) {
                Instructor userInstructor = userService.ensureInstructorProfile(user);
                if (userInstructor != null && !course.getInstructor().getId().equals(userInstructor.getId())) {
                    throw new BusinessException("You can only edit your own courses");
                }
            }
        }
        
        Integer instructorId = dto.instructorId();
        if (instructorId != null) {
            Instructor instructor = instructorRepository.findById(instructorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id " + instructorId));
            course.setInstructor(instructor);
        }
        course.setTitle(dto.title());
        course.setDescription(dto.description());

        if (dto.price() != null) {
            course.setPrice(dto.price());
        }

        int seats = resolveTotalSeats(dto);
        if (seats > 0) {
            int reservedSeats = Math.max(0, course.getCapacity() - course.getSeatsRemaining());
            course.setCapacity(seats);
            course.setSeatsRemaining(Math.max(0, seats - reservedSeats));
        }

        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Integer id) {
        deleteCourse(id, null);
    }

    @Transactional
    public void deleteCourse(Integer id, Authentication authentication) {
        Integer courseId = Objects.requireNonNull(id, "Course id must not be null");
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + id));
        
        // Check authorization: INSTRUCTOR can only delete their own courses
        if (authentication != null) {
            AppUser user = userService.findByEmail(authentication.getName());
            if (user != null && user.getRole() == UserRole.instructor) {
                Instructor userInstructor = userService.ensureInstructorProfile(user);
                if (userInstructor != null && !course.getInstructor().getId().equals(userInstructor.getId())) {
                    throw new BusinessException("You can only delete your own courses");
                }
            }
        }
        
        courseRepository.deleteById(courseId);
    }

    @Transactional
    public void updateAvailableSeatsAfterReservation(Course course) {
        courseRepository.save(Objects.requireNonNull(course, "Course must not be null"));
    }

    private int resolveTotalSeats(CourseDTO dto) {
        if (dto.totalSeats() != null && dto.totalSeats() > 0) {
            return dto.totalSeats();
        }
        if (dto.capacity() != null && dto.capacity() > 0) {
            return dto.capacity();
        }
        return 1;
    }
}
