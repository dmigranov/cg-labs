package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.model.WireframeLine;

import java.util.List;

public abstract class Primitive {
    private double kDR;
    private double kDG;
    private double kDB;       //коэффициенты диффузного и рассеянного отражения
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
}
