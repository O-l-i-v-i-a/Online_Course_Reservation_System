package com.example.onlinecourse.service;

import com.example.onlinecourse.domain.AppUser;
import com.example.onlinecourse.domain.Instructor;
import com.example.onlinecourse.domain.Student;
import com.example.onlinecourse.domain.UserRole;
import com.example.onlinecourse.exception.BusinessException;
import com.example.onlinecourse.exception.ResourceNotFoundException;
import com.example.onlinecourse.repository.AppUserRepository;
import com.example.onlinecourse.repository.InstructorRepository;
import com.example.onlinecourse.repository.StudentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository appUserRepository,
                       InstructorRepository instructorRepository,
                       StudentRepository studentRepository,
                       PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.instructorRepository = instructorRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public AppUser findByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        return appUserRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + normalizedEmail));
    }

    @Transactional
    public AppUser createUser(String name, String email, String rawPassword, String role, String phone, String specialization) {
        String normalizedEmail = normalizeEmail(email);
        if (appUserRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new BusinessException("A user already exists with email " + normalizedEmail);
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new BusinessException("Password is required");
        }

        AppUser user = new AppUser();
        user.setName(cleanText(name));
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(parseRole(role));

        AppUser savedUser = appUserRepository.save(user);
        ensureProfileForUser(savedUser, phone, specialization);
        return savedUser;
    }

    @Transactional
    public void ensureProfileForEmail(String email) {
        AppUser user = findByEmail(email);
        ensureProfileForUser(user, null, null);
    }

    @Transactional
    public void ensureProfileForAuthentication(Authentication authentication) {
        if (authentication != null) {
            ensureProfileForEmail(authentication.getName());
        }
    }

    @Transactional
    public Instructor ensureInstructorProfile(AppUser user) {
        if (user == null) {
            throw new BusinessException("User must not be null");
        }
        if (user.getRole() != UserRole.instructor) {
            throw new BusinessException("User is not an instructor");
        }
        return instructorRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Instructor instructor = new Instructor();
                    instructor.setUser(user);
                    instructor.setSpecialization("General");
                    return instructorRepository.save(instructor);
                });
    }

    @Transactional
    public Student ensureStudentProfile(AppUser user) {
        if (user == null) {
            throw new BusinessException("User must not be null");
        }
        if (user.getRole() != UserRole.student) {
            throw new BusinessException("User is not a student");
        }
        return studentRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Student student = new Student();
                    student.setUser(user);
                    student.setPhone("0000000000");
                    return studentRepository.save(student);
                });
    }

    @Transactional
    public void ensureProfileForUser(AppUser user, String phone, String specialization) {
        if (user == null) {
            return;
        }

        if (user.getRole() == UserRole.instructor) {
            Instructor instructor = instructorRepository.findByUser_Id(user.getId())
                    .orElseGet(() -> {
                        Instructor profile = new Instructor();
                        profile.setUser(user);
                        profile.setSpecialization(hasText(specialization) ? specialization.trim() : "General");
                        return instructorRepository.save(profile);
                    });

            if (!hasText(instructor.getSpecialization()) && hasText(specialization)) {
                instructor.setSpecialization(specialization.trim());
                instructorRepository.save(instructor);
            }
            return;
        }

        if (user.getRole() == UserRole.student) {
            Student student = studentRepository.findByUser_Id(user.getId())
                    .orElseGet(() -> {
                        Student profile = new Student();
                        profile.setUser(user);
                        profile.setPhone(hasText(phone) ? phone.trim() : "0000000000");
                        return studentRepository.save(profile);
                    });

            if (!hasText(student.getPhone()) && hasText(phone)) {
                student.setPhone(phone.trim());
                studentRepository.save(student);
            }
        }
    }

    @Transactional
    public void ensureProfileForUser(AppUser user) {
        ensureProfileForUser(user, null, null);
    }

    private UserRole parseRole(String role) {
        if (role == null || role.isBlank()) {
            throw new BusinessException("Role is required");
        }

        try {
            return UserRole.valueOf(role.trim().toLowerCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid role: " + role);
        }
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessException("Email is required");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String cleanText(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}