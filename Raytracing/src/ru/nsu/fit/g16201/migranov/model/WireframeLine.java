package ru.nsu.fit.g16201.migranov.model;

public class WireframeLine {
    private Point3D p1;
    private Point3D p2;
    public WireframeLine(Point3D p1, Point3D p2)
    {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point3D getP1() {
        return p1;
    }

    public Point3D getP2() {
        return p2;
    }
}
