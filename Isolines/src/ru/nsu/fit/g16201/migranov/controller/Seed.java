package ru.nsu.fit.g16201.migranov.controller;

import java.awt.*;

public class Seed {
    public int color;
    public double x;
    public double y;

    public Seed(Color color, double x, double y)
    {
        this.color = color.getRGB();
        this.x = x; this.y = y;
    }
}
