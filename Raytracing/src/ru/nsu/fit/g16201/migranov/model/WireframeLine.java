package ru.nsu.fit.g16201.migranov.model;

import java.util.List;

public class WireframeLine {
    //private Point3D p1;
    //private Point3D p2;

    private Point3D[] line;

    public WireframeLine(Point3D ... points)
    {
        line = points;
    }

    public Point3D[] getPoints() {
        return line;
    }

}
