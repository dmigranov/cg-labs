package ru.nsu.fit.g16201.migranov.model;


import java.awt.*;
import java.awt.geom.Point2D;
import java.util.function.BiFunction;

public class Model {
    private double a = -200, b = 200, c = -200, d = 200;  //область определения
    private int k, m;
    private double[][] grid;
    private double minValue = Double.MAX_VALUE, maxValue = Double.MIN_VALUE;
    private BiFunction<Double, Double, Double> function = (x, y) -> x*x + y*y;

    public Model(int k, int m)  //k - число значений сетки по x, m - по y
    {
        this.m = m;
        this.k = k;
        grid = new double[m][k];
        calculateGrid();
    }

    public Model(int k, int m, BiFunction<Double, Double, Double> function, double a, double b, double c, double d)  //k - число значений сетки по x, m - по y
    {
        this.function = function;
        this.m = m;
        this.k = k;
        grid = new double[m][k];
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;


        calculateGrid();
    }

    private void calculateGrid() {
        for(int i = 0; i < m; i++)
            for(int j = 0; j < k; j++)
            {
                double y = m > 1 ? c + i*(d - c)/(m - 1) : (d - c)/2;
                double x = k > 1 ? a + j*(b - a)/(k - 1) : (b - a)/2;
                double value = function.apply(x, y);
                grid[i][j] = value;
                if(value < minValue)
                    minValue = value;
                if(value > maxValue)
                    maxValue = value;
            }
    }


    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getMinValue()
    {
        return minValue;
    }

    public double getMaxValue()
    {
        return maxValue;
    }

    public double getM() {
        return m;
    }

    public double getK() {
        return k;
    }


    //xj yi
    public double getValue(int j, int i)
    {
        return grid[i][j];
    }

    public Point2D getPoint(int j, int i) {
        double y = m > 1 ? c + i*(d - c)/(m - 1) : (d - c)/2;
        double x = k > 1 ? a + j*(b - a)/(k - 1) : (b - a)/2;
        return new Point2D.Double(x, y);
    }

}
