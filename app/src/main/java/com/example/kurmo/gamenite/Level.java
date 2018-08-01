package com.example.kurmo.gamenite;

public class Level {
    private int level;
    private int xp;

    public Level() {

    }
    public Level(int level, int xp)  {
        this.level = level;
        this.xp = xp;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void setXp(int xp) {
        this.xp = xp;
    }
}
