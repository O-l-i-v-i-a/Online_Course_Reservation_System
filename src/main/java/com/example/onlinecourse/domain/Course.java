package com.example.onlinecourse.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "COURSE")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`CourseId`")
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "`InstructorId`", nullable = false)
    private Instructor instructor;

    @Column(name = "`Title`", nullable = false, length = 200)
    private String title;

    @Column(name = "`Description`")
    private String description;

    @Column(name = "`Price`", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "`TotalSeats`", nullable = false)
    private int totalSeats;

    @Column(name = "`AvailableSeats`", nullable = false)
    private int availableSeats;

    public Course() {}

    public Course(Integer id, Instructor instructor, String title, String description, BigDecimal price, int capacity) {
        this.id = id;
        this.instructor = instructor;
        this.title = title;
        this.description = description;
        this.price = price;
        this.totalSeats = capacity;
        this.availableSeats = capacity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getCapacity() {
        return totalSeats;
    }

    public void setCapacity(int capacity) {
        this.totalSeats = capacity;
        if (this.availableSeats > capacity) {
            this.availableSeats = capacity;
        }
    }

    public int getSeatsRemaining() {
        return availableSeats;
    }

    public void setSeatsRemaining(int seatsRemaining) {
        this.availableSeats = seatsRemaining;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        setCapacity(totalSeats);
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public boolean takeSeat() {
        if (availableSeats <= 0) {
            return false;
        }
        availableSeats -= 1;
        return true;
    }

    public void releaseSeat() {
        if (availableSeats < totalSeats) {
            availableSeats += 1;
        }
    }

    @PrePersist
    public void prePersist() {
        // Ensure seatsRemaining equals capacity when a new course is created
        if (availableSeats == 0 && totalSeats > 0) {
            availableSeats = totalSeats;
        }
    }
}
