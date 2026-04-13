package com.example.onlinecourse.repository;

import com.example.onlinecourse.domain.Enrollment;
import com.example.onlinecourse.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByStudentId(Integer studentId);

    @Query("""
            select case when count(e) > 0 then true else false end
            from Enrollment e join e.courses c
            where e.student.id = :studentId
              and c.id = :courseId
              and e.status in :activeStatuses
            """)
    boolean existsActiveReservationForCourse(@Param("studentId") Integer studentId,
                                             @Param("courseId") Integer courseId,
                                             @Param("activeStatuses") Collection<ReservationStatus> activeStatuses);
}
