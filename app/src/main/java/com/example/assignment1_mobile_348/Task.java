package com.example.assignment1_mobile_348;

public class Task {

    private String description;
    private boolean status;

    public Task() {
        this.description = "Null";
        this.status = false;
    }
    public Task(String description, boolean status) {
        this.description = description;
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}