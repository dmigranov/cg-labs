package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        return new Point3D(center.x - radius, center.y - radius, center.z - radius);
    }

    @Override
    public Point3D getMaxPoint() {
        return new Point3D(center.x + radius, center.y + radius, center.z + radius);
    }

    @Override
    public List<WireframeLine> getWireframeLines() {
        List<WireframeLine> lines = new ArrayList<>();

        int n = 6; int m = 6; int k = 10;

        WireframeLine[] perLines = new WireframeLine[m*k + 1];
        for (int i = 0; i <= m*k; i++)
            perLines[i] = new WireframeLine();
        for (int i = 0; i <= n*k; i++)
        {
            double delta = (double)i/n/k;
            double ux = (center.z - radius) * (1 - delta) + (center.z + radius) * delta;
            double uy = Math.sqrt(Math.pow(radius, 2) - Math.pow(center.z - ux, 2));
            WireframeLine parLine = new WireframeLine();
            for (int j = 0; j <= m * k; j++) {
                double v = (Math.PI * 2) * j / m / k;
                double x = uy * Math.cos(v);
                double y = uy * Math.sin(v);
                double z = ux;

                if(i % k == 0)
                    parLine.addPoint(new Point3D(x, y, z));
                if(j % k == 0)
                    perLines[j].addPoint(new Point3D(x, y, z));
            }
            lines.add(parLine);
        }
        lines.addAll(Arrays.asList(perLines));


        return lines;
    }

    private Sphere(Sphere worldPrimitive)
    {
        super(worldPrimitive);
    }

    @Override
    public Primitive movePrimitive(Matrix matrix) {
        Sphere returnSphere = new Sphere(this);

        returnSphere.center = matrix.applyMatrix(center);
        returnSphere.radius = radius;

        return returnSphere;
    }

    @Override
    public IntersectionNormal findIntersection(Point3D r0, Point3D rd) {
        Point3D oc = Point3D.subtract(center, r0);      //вообще r0 всегда 0..

        double ocLen2 = Math.pow(oc.x, 2) + Math.pow(oc.y, 2) + Math.pow(oc.z, 2);

        double t = 0;

        if(ocLen2 > Math.pow(radius, 2))
        {
            //начало луча снаружи сферы!
            double tca = Point3D.getScalarProduct(oc, rd);  //расстояние от начала луча до ближайшей к центру сферы точки

            if(tca < 0)
                return null;    //луч не пересекает сферу
            else
            {
                double thc2 = Math.pow(radius, 2) - (ocLen2 - tca*tca);
                if(thc2 < 0)
                    return null;
                t = tca - Math.sqrt(thc2);
            }
        }
        else
        {
            //todo: случай когда камера внутри сферы?
            return null;
        }

        System.out.println("HEre");
        Point3D ri = Point3D.add(r0, Point3D.multiplyByScalar(t, rd));
        Point3D ni = Point3D.multiplyByScalar(1/radius, Point3D.subtract(ri, center));

        return new IntersectionNormal(ri, ni);
    }
}
