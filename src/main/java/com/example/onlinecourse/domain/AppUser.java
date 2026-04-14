package com.example.onlinecourse.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "`USER`")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`UserId`")
    private Integer id;

    @Column(name = "`Name`", nullable = false, length = 100)
    private String name;

    @Column(name = "`Email`", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "`Password`", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "`Role`", nullable = false)
    private UserRole role;

    public AppUser() {
    }

    public AppUser(Integer id, String name, String email, String password, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
