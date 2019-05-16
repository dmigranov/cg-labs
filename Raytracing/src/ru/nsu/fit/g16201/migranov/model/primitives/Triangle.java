package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.IntersectionNormal;
import ru.nsu.fit.g16201.migranov.model.Matrix;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.model.WireframeLine;

import java.util.ArrayList;
import java.util.List;

public class Triangle extends Primitive {
    private Point3D p1;
    private Point3D p2;
    private Point3D p3;

    public Triangle(Point3D p1, Point3D p2, Point3D p3)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    @Override
    public Point3D getMinPoint() {
        double x = Math.min(Math.min(p1.x, p2.x), p3.x);
        double y = Math.min(Math.min(p1.y, p2.y), p3.y);
        double z = Math.min(Math.min(p1.z, p2.z), p3.z);

        return new Point3D(x, y, z);
    }

    @Override
    public Point3D getMaxPoint() {
        double x = Math.max(Math.max(p1.x, p2.x), p3.x);
        double y = Math.max(Math.max(p1.y, p2.y), p3.y);
        double z = Math.max(Math.max(p1.z, p2.z), p3.z);

        return new Point3D(x, y, z);
    }

    @Override
    public List<WireframeLine> getWireframeLines() {
        List<WireframeLine> lines = new ArrayList<>();
        lines.add(new WireframeLine(p1, p2, p3, p1));

        return lines;
    }

    private Triangle(Triangle worldPrimitive)
    {
        super(worldPrimitive);
    }

    @Override
    public Primitive movePrimitive(Matrix matrix)
    {
        Triangle returnTri = new Triangle(this);

        returnTri.p1 = matrix.applyMatrix(p1);
        returnTri.p2 = matrix.applyMatrix(p2);
        returnTri.p3 = matrix.applyMatrix(p3);

        return returnTri;
    }

    @Override
    public IntersectionNormal findIntersection(Point3D r0, Point3D rd) {
        return null;
    }
}
