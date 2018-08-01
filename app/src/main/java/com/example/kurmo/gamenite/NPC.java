package com.example.kurmo.gamenite;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NPC {
    public String name;
    public int level;
    public int attack;
    public int gold;
    public int defence;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();


    public NPC() {
    }
    public NPC(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
    public void setlevel(int level) {this.level = level;}
    public int getlevel() {return level;}

    public int getattack() {
        return attack;
    }
    public int getgold() {
        return gold;
    }
    public void setgold(int gold) {
        this.gold = gold;
    }
    public int getdefence() {
        return defence;
    }
    public void setattack(int attack) {this.attack = attack;}
    public void setdefence(int defence) {this.defence = defence;}
}
