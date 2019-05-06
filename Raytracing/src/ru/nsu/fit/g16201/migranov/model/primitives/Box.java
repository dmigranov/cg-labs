package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.model.WireframeLine;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<WireframeLine> getWireframeLines() {
        Point3D p000 = minPoint, p111 = maxPoint;
        double x0 = p000.x, y0 = p000.y, z0 = p000.z;
        double x1 = p111.x, y1 = p111.y, z1 = p111.z;

        Point3D p100 = new Point3D(x1, y0, z0);
        Point3D p010 = new Point3D(x0, y1, z0);
        Point3D p001 = new Point3D(x0, y0, z1);


        //todo

        return null;
    }
}
