package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.Point3D;

public class Sphere extends Primitive {
    private Point3D center;
    private double radius;
    public Sphere(Point3D center, double radius, double kDR, double kDG, double kDB, double kSR, double kSG, double kSB, double power)
    {
        super(kDR, kDG, kDB, kSR, kSG, kSB, power);

        this.center = center;
        this.radius = radius;
    }

    public Sphere(Point3D center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public Point3D getMinPoint() {
        return Point3D.add();
    }

    @Override
    public Point3D getMaxPoint() {
        return null;
    }
}
