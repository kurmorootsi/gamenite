package com.example.kurmo.gamenite;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Tree {
    private String name;
    private int level;
    private int gold;
    private int id;
    private int time;
    private int xp;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();


    public Tree() {
    }
    public Tree(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {this.name = name;}
    public void setlevel(int level) {this.level = level;}
    public int getlevel() {return level;}
    public void setXp(int xp) {
        this.xp = xp;
    }
    public void setTime(int time) {this.time = time;}
    public int getTime() {return time;}
    public int getXp() {
        return xp;
    }
    public int getId() {
        return id;
    }
    public int getgold() {
        return gold;
    }
    public void setgold(int gold) {
        this.gold = gold;
    }
}
