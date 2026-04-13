package com.example.onlinecourse.repository;

import com.example.onlinecourse.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
	Optional<Student> findByUser_Id(Integer userId);
}
