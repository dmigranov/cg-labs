package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.IntersectionNormal;
import ru.nsu.fit.g16201.migranov.model.Matrix;
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
        Point3D p110 = new Point3D(x1, y1, z0);
        Point3D p011 = new Point3D(x0, y1, z1);
        Point3D p101 = new Point3D(x1, y0, z1);

        List<WireframeLine> lines = new ArrayList<>();

        lines.add(new WireframeLine(p000, p010, p110, p100, p000));
        lines.add(new WireframeLine(p001, p011, p111, p101, p001));
        lines.add(new WireframeLine(p101, p100));
        lines.add(new WireframeLine(p001, p000));
        lines.add(new WireframeLine(p111, p110));
        lines.add(new WireframeLine(p011, p010));

        return lines;
    }


    private Box(Box worldPrimitive)
    {
        super(worldPrimitive);
    }

    @Override
    public Primitive movePrimitive(Matrix matrix)
    {
        Box returnBox = new Box(this);

        returnBox.minPoint = matrix.applyMatrix(minPoint);
        returnBox.maxPoint = matrix.applyMatrix(maxPoint);

        return returnBox;
    }

    @Override
    public IntersectionNormal findIntersection(Point3D r0, Point3D rd) {
        double tNear = -Double.MAX_VALUE, tFar = Double.MAX_VALUE;


        double minX = minPoint.x, maxX = maxPoint.x;
        //x
        {
            if (rd.x == 0) {
                if (r0.x < minX  || r0.x > maxX)
                    return null;
            }
            else {
                double t1 = (minX - r0.x)/rd.x;
                double t2 = (maxX - r0.x)/rd.x;
                if(t2 < t1)
                {
                    double temp = t1;
                    t1 = t2;
                    t2 = temp;
                }   //t2 >= t1 теперь
                if(t1 > tNear)
                    tNear = t1;
                if(t2 < tFar)
                    tFar = t2;
                System.out.println("yo");

                if(tNear > tFar)
                    return null;    //а оно вообще сюда попадет? ведь мы уже поменяли t1 и t2 и t2 > t1...
                if(tFar < 0)
                    return null;
            }
        }

        return null;
    }
}
