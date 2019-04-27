package ru.nsu.fit.g16201.migranov.model;

import java.awt.*;

public class Light {
    private Point3D center;
    private Color color;

    public Light(Point3D center, Color color)
    {
        this.center = center;
        this.color = color;
    }
}
