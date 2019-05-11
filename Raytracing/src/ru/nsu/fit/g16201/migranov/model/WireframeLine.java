package ru.nsu.fit.g16201.migranov.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WireframeLine {
    private List<Point3D> line;

    public WireframeLine(Point3D ... points)
    {
        line = new ArrayList<>();
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
