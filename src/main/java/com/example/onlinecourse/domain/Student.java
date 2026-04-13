package com.example.onlinecourse.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "STUDENT")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`StudentId`")
    private Integer id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "`UserId`", nullable = false)
    private AppUser user;

    @Column(name = "`Phone`", length = 20)
    private String phone;

    public Student() {}

    public Student(Integer id, AppUser user, String phone) {
        this.id = id;
        this.user = user;
        this.phone = phone;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
