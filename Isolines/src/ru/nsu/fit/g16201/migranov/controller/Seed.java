package ru.nsu.fit.g16201.migranov.controller;

import java.awt.*;

class Seed {
    int color;
    double x;
    double y;

    Seed(Color color, double x, double y)
    {
        this.color = color.getRGB();
        this.x = x; this.y = y;
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
