package com.example.onlinecourse.domain;

/**
 * Domain object kept purely in memory for the demo; no JPA annotations.
 */
public class Course {
    private Integer id;
    private String title;
    private String description;
    private int capacity;
    private int seatsRemaining;

    public Course() {}

    public Course(Integer id, String title, String description, int capacity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.capacity = capacity;
        this.seatsRemaining = capacity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
