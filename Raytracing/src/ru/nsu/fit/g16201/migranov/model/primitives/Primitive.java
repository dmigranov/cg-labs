package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.Point3D;

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

    public abstract Point3D getMinPoint();
    public abstract Point3D getMaxPoint();



}
