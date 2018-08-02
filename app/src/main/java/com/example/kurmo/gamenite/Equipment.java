package com.example.kurmo.gamenite;

public class Equipment {
    private int itemID;
    private String name;
    private int level;
    private int attack;
    private int defence;
    private int gold;

    public Equipment() {}
    public Equipment(int itemID) {this.itemID = itemID;}
    public Equipment(int itemID, String name, int level, int attack, int defence, int gold) {
        this.itemID = itemID;
        this.name = name;
        this.level = level;
        this.attack = attack;
        this.defence = defence;
        this.gold = gold;
    }
    //Setters
    public void setAttack(int attack) {
        this.attack = attack;
    }
    public void setDefence(int defence) {
        this.defence = defence;
    }
    public void setLevel(int level) {this.level = level;}
    public void setItemID(int itemID) {
        this.itemID = itemID;
    }
    public void setName(String name) {this.name = name;}
    public void setGold(int gold) {this.gold = gold;}

    //Getters
    public int getGold() {
        return gold;
    }
    public int getAttack() {return attack;}
    public int getDefence() {return defence;}
    public int getLevel() {return level;}
    public int getItemID() {return itemID;}
    public String getName() {return name;}

}
