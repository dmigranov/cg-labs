package ru.nsu.fit.g16201.migranov.model;

public class IntersectionNormal {
    public Point3D intersectionPoint;
    public Point3D normalVector;

    public IntersectionNormal(Point3D intersectionPoint, Point3D normalVector)
    {
        this.intersectionPoint = intersectionPoint;
        this.normalVector = normalVector;
    }
}
