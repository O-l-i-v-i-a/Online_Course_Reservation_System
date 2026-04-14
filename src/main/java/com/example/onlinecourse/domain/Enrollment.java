package com.example.onlinecourse.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "RESERVATION")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`ReservationID`")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "`Status`", nullable = false)
    private ReservationStatus status;

    @Column(name = "`ReservationDate`", nullable = false)
    private LocalDate enrollmentDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "`StudentID`", nullable = false)
    private Student student;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "RESERVATION_COURSE",
            joinColumns = @JoinColumn(name = "`ReservationID`"),
            inverseJoinColumns = @JoinColumn(name = "`CourseID`")
    )
    private Set<Course> courses = new LinkedHashSet<>();

    public Enrollment() {}

    public Enrollment(Integer id, Student student, Set<Course> courses) {
        this.id = id;
        this.student = student;
        this.courses = courses;
        this.enrollmentDate = LocalDate.now();
        this.status = ReservationStatus.pending;
    }

    public Integer getId() {
        return id;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    @JsonIgnore
    public Course getPrimaryCourse() {
        return courses.stream().findFirst().orElse(null);
    }

    public boolean drop() {
        if (ReservationStatus.cancelled == status) {
            return false;
        }
        status = ReservationStatus.cancelled;
        courses.forEach(Course::releaseSeat);
        return true;
    }
}
