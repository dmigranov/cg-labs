package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.Point3D;

public class Quadrangle extends Primitive {
    private Point3D p1;
    private Point3D p2;
    private Point3D p3;
    private Point3D p4;

    public Quadrangle(Point3D p1, Point3D p2, Point3D p3, Point3D p4) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }
}
