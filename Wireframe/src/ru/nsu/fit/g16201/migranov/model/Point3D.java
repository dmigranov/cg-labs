package ru.nsu.fit.g16201.migranov.model;

public class Point3D {
    public double x, y, z;

    public Point3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Point3D getVectorProduct(Point3D up, Point3D w) {

    }

    public Point3D normalize()
    {
        double len = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        return new Point3D(x/len, y/len, z/len);
    }
}
