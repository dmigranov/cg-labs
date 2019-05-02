package ru.nsu.fit.g16201.migranov.model.primitives;

import ru.nsu.fit.g16201.migranov.model.Point3D;

public class Sphere extends Primitive {
    public Sphere(Point3D center, double radius, double kDR, double kDG, double kDB, double kSR, double kSG, double kSB, double power)
    {
        super(kDR, kDG, kDB, kSR, kSG, kSB, power);

    }

}
