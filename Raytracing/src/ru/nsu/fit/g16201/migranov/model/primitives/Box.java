package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.Point3D;

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
    public List<List<Point3D>> getWireframeLines() {
        Point3D p000 = minPoint, p111 = maxPoint;
        Point3D

        List<List<Point3D>> lines = new ArrayList<>();
        List<Point3D> line1 = new ArrayList<>();
        line1.add(p1);
        line1.add(p2);
        line1.add(p3);
        line1.add(p4);
        lines.add(line);
        return lines;
    }
}
