package ru.nsu.fit.g16201.migranov.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WireframeLine {
    //private Point3D p1;
    //private Point3D p2;

    private List<Point3D> line;

    public WireframeLine(Point3D ... points)
    {
        line = new ArrayList<Point3D>();
        line.addAll(Arrays.asList(points));
    }

    public List<Point3D> getPoints() {
        return line;
    }

    public void addPoint(Point3D p)
    {
        line.add(p);
    }

}
