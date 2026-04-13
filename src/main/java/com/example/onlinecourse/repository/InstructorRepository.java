package com.example.onlinecourse.repository;

import com.example.onlinecourse.domain.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
	Optional<Instructor> findByUser_Id(Integer userId);
}
