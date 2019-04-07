package ru.nsu.fit.g16201.migranov.model;

import java.awt.*;
import java.util.List;

public class Figure {
    private Color color;
    private Point3D center;
    private Matrix rotateMatrix;
    private List<Point2D> splinePoints;


    public Figure(Point3D center, Color color, Matrix rotateMatrix, List<Point2D> splinePoints) {
        this.center = center;
        this.color = color;
        this.rotateMatrix = rotateMatrix;
        this.splinePoints = splinePoints;
    }


    //todo: может стоит сделать дек чтобы можно добавлять и в конец и в анчало?
    public List<Point2D> getSplinePoints() {
        return splinePoints;
    }
}
