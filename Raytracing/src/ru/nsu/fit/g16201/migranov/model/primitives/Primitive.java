package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.IntersectionNormal;
import ru.nsu.fit.g16201.migranov.model.Matrix;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.model.WireframeLine;

import java.util.List;

public abstract class Primitive {
    private double kDR, kDG, kDB;       //коэффициенты диффузного И РАССЕЯННОГО (ambient) отражения
    private double kSR;
    private double kSG;
    private double kSB;       //коэффициенты зеркального отражения
    private double power;

    public Primitive(double kDR, double kDG, double kDB, double kSR, double kSG, double kSB, double power)
    {
        this.kDR = kDR;
        this.kDG = kDG;
        this.kDB = kDB;
        this.kSR = kSR;
        this.kSG = kSG;
        this.kSB = kSB;
        this.power = power;
    }

    Primitive()
    {

    }

    public void setOpticParameters(double kDR, double kDG, double kDB, double kSR, double kSG, double kSB, double power)
    {
        this.kDR = kDR;
        this.kDG = kDG;
        this.kDB = kDB;
        this.kSR = kSR;
        this.kSG = kSG;
        this.kSB = kSB;
        this.power = power;
    }

    //вовзращают минимальные и максимальные координаты (понятно, по отдельности)
    public abstract Point3D getMinPoint();
    public abstract Point3D getMaxPoint();

    //по идее, можно считать один раз при загрузке
    //а WireframeLine плохо в том отношении, что два раза для одной точки будем переводить матрицами!
    public abstract List<WireframeLine> getWireframeLines();

    public Primitive(Primitive worldPrimitive)
    {
        kDR = worldPrimitive.kDR;
        kDG = worldPrimitive.kDG;
        kDB = worldPrimitive.kDB;

        kSR = worldPrimitive.kSR;
        kSG = worldPrimitive.kSG;
        kSB = worldPrimitive.kSB;

        power = worldPrimitive.power;
    }

    public abstract Primitive movePrimitive(Matrix matrix);

    public abstract IntersectionNormal findIntersection(Point3D r0, Point3D rd);

    public double[] getDiffuseAmbientCharacteristics()
    {
        return new double[] {kDR, kDG, kDB};
    }

    public double getPower() {
        return power;
    }

    public double[] getSpecularCharacteristics() {
        return new double[] {kSR, kSG, kSB};
    }

    public Point3D getNormal(Point3D p0, Point3D p1, Point3D p2)
    {
        double A = p0.y * (p1.z - p2.z) + p0.z * (p2.y - p1.y) + p1.y * p2.z - p1.z * p2.y;
        double B = -(p0.x * (p1.z - p2.z) + p0.z * (p2.x - p1.x) + p1.x * p2.z - p1.z * p2.x);
        double C = (p0.x * (p1.y - p2.y) + p0.y * (p2.x - p1.x) + p1.x * p2.y - p1.y * p2.x);
        return new Point3D(A, B, C).normalize();
    }
}
