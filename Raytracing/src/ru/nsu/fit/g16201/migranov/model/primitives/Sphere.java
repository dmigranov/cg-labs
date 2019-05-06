package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.Matrix;
import ru.nsu.fit.g16201.migranov.model.Point2D;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.model.WireframeLine;

import java.util.ArrayList;
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
        //todo

        List<WireframeLine> lines = new ArrayList<>();

        int n = 5; int m = 5; //k?

        for (int i = 0; i < n; i++)
        {
            double delta = (double)i/n;
            double ux = (center.z - radius) * (1 - delta) + (center.z + radius) * delta;
            double uy = Math.sqrt(Math.pow(radius, 2) - Math.pow(center.z - ux, 2));
            WireframeLine parLine = new WireframeLine();
            for (int j = 0; j <= m /** k*/; j++) {
                double v = (Math.PI * 2) * j / m /*/ k*/;
                double x = uy * Math.cos(v);
                double y = uy * Math.sin(v);
                double z = ux;


                /*Matrix p = new Matrix(4, 1, x, y, z, 1);

                Matrix np = Matrix.multiply(rtm, p);                        //на самом деле произведение r и t имеет простой вид - можно упростить
                double nx = np.get(0, 0), ny = np.get(1, 0), nz = np.get(2, 0);
                //modelPoints[i][j] = new Point3D(nx, ny, nz);
                modelPoints[i][j] = new Point3D(x, y, z);*/

            }
        }


        return null;
    }
}
