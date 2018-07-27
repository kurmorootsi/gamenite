package com.example.kurmo.gamenite;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {
    public String userId;
    public int level;
    public int experience;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();


    public User() {

    }

    public User(String userId) {
        this.userId = userId;
    }
    public void setuserId(String userId) {
        this.userId = userId;
    }
    public void setlevel(int level) {this.level = level;}
    public void setexperience(int experience) {this.experience = experience;}
    public String getuserId() {return userId;}
    public int getexperience() {
        return experience;
    }
    public int getlevel() {return level;}
    public void addexperience(int experience) {
        this.experience += experience;
        database.child("users").child(userId).child("experience").setValue(this.experience);
    }
    public void addlevel() {this.level++;database.child("users").child(userId).child("level").setValue(this.level);}
}
