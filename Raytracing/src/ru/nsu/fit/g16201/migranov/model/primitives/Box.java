package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.Point3D;

public class Box extends Primitive {
    private Point3D minPoint;
    private Point3D maxPoint;

    public Box(Point3D minPoint, Point3D maxPoint) {
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
    }

    @Override
    public Point3D getMinPoint() {
        return minPoint;
    }

    @Override
    public Point3D getMaxPoint() {
        return maxPoint;
    }
}
