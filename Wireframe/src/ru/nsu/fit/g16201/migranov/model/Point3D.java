package ru.nsu.fit.g16201.migranov.model;

public class Point3D {
    public double x, y, z;

    public Point3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D normalize(Point3D p)
    {
        double len = Math.sqrt(Math.pow(p.x, 2) + Math.pow(p.y, 2) + Math.pow(p.z, 2));
    }
}
