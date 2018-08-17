package com.example.kurmo.gamenite;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

@IgnoreExtraProperties
public class User{
    private boolean isLoaded;
    private String userId;
    private boolean isFighting;
    private int level;
    private int last_opp;
    private int seekbar;
    private int gold;
    private int attack;
    private int wclevel;
    private int wclog;
    private int defence;
    public int experience;
    private Long countdown;
    private Long number;

    public User() {
    }
    public User(boolean isFighting, boolean isLoaded, int level, int gold, int attack, int defence, int experience, Long countdown, Long number) {
        this.isLoaded = isLoaded; this.isFighting = isFighting; this.level = level; this.gold = gold; this.attack = attack; this.defence = defence; this.experience = experience; this.countdown = countdown; this.number = number;
    }
    public int getLast_opp() {return last_opp;}
    public void setLast_opp(int last_opp) {this.last_opp = last_opp;}
    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }
    public void setFighting(boolean isFighting) {
        this.isFighting = isFighting;
    }
    public boolean getFighting() {return isFighting;}
    public void setCountdown(Long countdown) {this.countdown = countdown;}

    public Long getCountdown() {return countdown;}
    public void setNumber(Long number) {this.number = number;}

    public Long getNumber() {return number;}
    public void setWclevel(int wclevel) {this.wclevel = wclevel;}
    public void setWclog(int wclog) {this.wclog = wclog;}

    public int getWclevel() {
        return wclevel;
    }

    public int getWclog() {
        return wclog;
    }
    public void addWclevel() {this.wclevel++;}
    public void addWclog(int wclog) {this.wclog += wclog;}
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
    public void setAttack(int attack) {
        this.attack = attack;
    }
    public void setDefence(int defence) {
        this.defence = defence;
    }
    public int getAttack() {return attack;}
    public int getDefence() {return defence;}
    public void addAttack(int attack) {
        this.attack += attack;
    }
    public void addDefence(int defence) {
        this.defence += defence;
    }
}
