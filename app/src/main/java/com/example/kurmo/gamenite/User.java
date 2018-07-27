package com.example.kurmo.gamenite;

public class User {
    public long userId;
    public String username;
    public String password;

    public User() {

    }

    public User(Long userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }
}
