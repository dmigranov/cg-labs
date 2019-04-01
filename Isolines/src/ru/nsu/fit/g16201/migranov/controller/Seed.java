package ru.nsu.fit.g16201.migranov.controller;

import java.awt.*;

class Seed {
    public static final int NONE = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int UP = 4;
    public static final int DOWN = 8;

    int color;
    int direction;
    double x;
    double y;

    Seed(Color color, double x, double y, int direction)
    {
        this.color = color.getRGB();
        this.x = x; this.y = y;
        this.direction = direction;
    }

    public Seed(Seed s1, Seed s2) {
        this.color = s1.color;
        this.x = (s1.x+s2.x)/2;
        this.y = (s1.y+s2.y)/2;
        this.direction = s1.direction | s2.direction;


    }

    @Override
    public boolean equals(Object obj)
    {
        Seed another = (Seed)obj;

        if(another.x == x && another.y == y)
            return true;
        return false;
    }

    @Override
    public int hashCode()
    {
        return (int)(x*y);
    }
}
