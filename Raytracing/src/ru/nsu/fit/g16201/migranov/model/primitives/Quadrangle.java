package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.IntersectionNormal;
import ru.nsu.fit.g16201.migranov.model.Matrix;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.model.WireframeLine;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Point3D getMinPoint() {
        double x = Math.min(Math.min(p1.x, p2.x), Math.min(p3.x, p4.x));
        double y = Math.min(Math.min(p1.y, p2.y), Math.min(p3.y, p4.y));
        double z = Math.min(Math.min(p1.z, p2.z), Math.min(p3.z, p4.z));

        return new Point3D(x, y, z);
    }

    @Override
    public Point3D getMaxPoint() {
        double x = Math.max(Math.max(p1.x, p2.x), Math.max(p3.x, p4.x));
        double y = Math.max(Math.max(p1.y, p2.y), Math.max(p3.y, p4.y));
        double z = Math.max(Math.min(p1.z, p2.z), Math.max(p3.z, p4.z));

        return new Point3D(x, y, z);
    }

    @Override
    public List<WireframeLine> getWireframeLines() {
        List<WireframeLine> lines = new ArrayList<>();
        lines.add(new WireframeLine(p1, p2, p3, p4, p1));

        return lines;
    }

    private Quadrangle(Quadrangle worldPrimitive)
    {
        super(worldPrimitive);
    }

    @Override
    public Primitive movePrimitive(Matrix matrix)
    {
        Quadrangle returnQuad = new Quadrangle(this);

        returnQuad.p1 = matrix.applyMatrix(p1);
        returnQuad.p2 = matrix.applyMatrix(p2);
        returnQuad.p3 = matrix.applyMatrix(p3);
        returnQuad.p4 = matrix.applyMatrix(p4);

        return returnQuad;
    }

    @Override
    public IntersectionNormal findIntersection(Point3D r0, Point3D rd) {
        return null;
    }
}
