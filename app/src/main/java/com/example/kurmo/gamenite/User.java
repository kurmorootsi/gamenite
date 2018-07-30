package com.example.kurmo.gamenite;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {
    public String userId;
    public int level;
    public int seekbar;
    public int gold;
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
    public int getgold() {
        return gold;
    }
    public void addgold(int gold) {
        this.gold += gold;
        database.child("users").child(userId).child("gold").setValue(this.gold);
    }
    public void setgold(int gold) {
        this.gold = gold;
        database.child("users").child(userId).child("gold").setValue(this.gold);
    }
    public void setseekbar(int progress) {
        this.seekbar = progress;
        database.child("users").child(userId).child("seekbar").setValue(this.seekbar);
    }
    public int getseekbar() {
        return this.seekbar;
    }
    public void addlevel() {this.level++;database.child("users").child(userId).child("level").setValue(this.level);}
}
