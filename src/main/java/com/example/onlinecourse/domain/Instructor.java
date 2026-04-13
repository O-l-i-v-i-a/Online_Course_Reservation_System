package com.example.onlinecourse.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "INSTRUCTOR")
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InstructorID")
    private Integer id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "UserID", nullable = false)
    private AppUser user;

    @Column(name = "Specialization", length = 150)
    private String specialization;

    public Instructor() {
    }

    public Instructor(Integer id, AppUser user, String specialization) {
        this.id = id;
        this.user = user;
        this.specialization = specialization;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
