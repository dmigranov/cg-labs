package ru.nsu.fit.g16201.migranov.model;

import ru.nsu.fit.g16201.migranov.model.primitives.Primitive;

import java.awt.*;

public class Light {
    private Point3D center;
    private Color color;

    public Light(Point3D center, Color color)
    {
        this.center = center;
        this.color = color;
    }

    public Light(Light worldLight, Matrix matrix)
    {

    }

}
