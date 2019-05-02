package ru.nsu.fit.g16201.migranov.model.primitives;

public abstract class Primitive {
    private double kDR, kDG, kDB;       //коэффициенты диффузного и рассеянного отражения
    private double kSR, kSG, kSB;       //коэффициенты зеркального отражения
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



}
