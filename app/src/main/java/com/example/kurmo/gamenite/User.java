package com.example.kurmo.gamenite;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;
@IgnoreExtraProperties
public class User {
    public boolean isLoaded;
    public String userId;
    public int level;
    public int seekbar;
    public int gold;
    public int experience;
    private Long countdown;
    private Long number;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();


    public User() {
    }

    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public void setCountdown(Long countdown) {this.countdown = countdown;}

    public Long getCountdown() {return countdown;}
    public void setNumber(Long number) {this.number = number;}

    public Long getNumber() {return number;}

    public User(String userId) {
        this.userId = userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setLevel(int level) {this.level = level;}
    public void setExperience(int experience) {this.experience = experience;}
    public String getUserId() {return userId;}
    public int getExperience() {
        return experience;
    }
    public int getLevel() {return level;}
    public void addExperience(int experience) {
        this.experience += experience;
    }
    public int getGold() {
        return gold;
    }
    public void addGold(int gold) {
        this.gold += gold;
    }
    public void setGold(int gold) {
        this.gold = gold;
    }
    public void setSeekbar(int progress) {
        this.seekbar = progress;
    }
    public int getSeekbar() {
        return this.seekbar;
    }
    public void addLevel() {this.level++;}
}
