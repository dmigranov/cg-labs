package ru.nsu.fit.g16201.migranov.controller;

import java.awt.*;
import java.awt.geom.Point2D;

public class Line {
    Point2D p1;
    Point2D p2;
    Color color;

    public Line(Point2D p1, Point2D p2, Color color)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
    }
}
