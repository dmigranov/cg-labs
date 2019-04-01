package ru.nsu.fit.g16201.migranov.controller;

import java.awt.*;

class Seed {
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int UP = 4;
    private static final int DOWN = 8;




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
