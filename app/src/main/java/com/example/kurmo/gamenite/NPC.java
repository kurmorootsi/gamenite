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

    public int getAttack() {
        return attack;
    }
    public int getgold() {
        return gold;
    }
    public void setgold(int gold) {
        this.gold = gold;
    }
    public int getDefence() {
        return defence;
    }
    public void setAttack(int attack) {}
    public void setDefence(int defence) {}
}
