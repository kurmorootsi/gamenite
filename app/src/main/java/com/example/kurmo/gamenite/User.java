package com.example.kurmo.gamenite;

public class User {
    public String userId;
    public int level;
    public int experience;

    public User() {

    }

    public User(String userId) {
        this.userId = userId;
        this.experience = 0;
        this.level = 0;
    }

    public int getExperience() {
        return experience;
    }
}
