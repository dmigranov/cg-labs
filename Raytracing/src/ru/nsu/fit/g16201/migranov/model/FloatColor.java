package ru.nsu.fit.g16201.migranov.model;

public class FloatColor {
    public double r, g, b;

    public FloatColor(double r, double g, double b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public FloatColor(int intColor) {
        r = (intColor & 0xFF0000) >> 16;
        g = (intColor & 0x00FF00) >> 8;
        b = intColor & 0x0000FF;
    }
}
