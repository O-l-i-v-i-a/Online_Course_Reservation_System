package com.example.onlinecourse.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "COURSE")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CourseID")
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "InstructorID", nullable = false)
    private Instructor instructor;

    @Column(name = "Title", nullable = false, length = 200)
    private String title;

    @Column(name = "Description")
    private String description;

    @Column(name = "Price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "TotalSeats", nullable = false)
    private int capacity;

    @Column(name = "AvailableSeats", nullable = false)
    private int seatsRemaining;

    public Course() {}

    public Course(Integer id, Instructor instructor, String title, String description, BigDecimal price, int capacity) {
        this.id = id;
        this.instructor = instructor;
        this.title = title;
        this.description = description;
        this.price = price;
        this.capacity = capacity;
        this.seatsRemaining = capacity;
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
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        if (this.seatsRemaining > capacity) {
            this.seatsRemaining = capacity;
        }
    }

    public int getSeatsRemaining() {
        return seatsRemaining;
    }

    public void setSeatsRemaining(int seatsRemaining) {
        this.seatsRemaining = seatsRemaining;
    }

    public boolean takeSeat() {
        if (seatsRemaining <= 0) {
            return false;
        }
        seatsRemaining -= 1;
        return true;
    }

    public void releaseSeat() {
        if (seatsRemaining < capacity) {
            seatsRemaining += 1;
        }
    }

    @PrePersist
    public void prePersist() {
        // Ensure seatsRemaining equals capacity when a new course is created
        if (seatsRemaining == 0 && capacity > 0) {
            seatsRemaining = capacity;
        }
    }
}
