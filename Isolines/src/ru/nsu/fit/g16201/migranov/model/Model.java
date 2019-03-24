package ru.nsu.fit.g16201.migranov.model;


import java.util.function.BiFunction;

public class Model {
    private double a = -200, b = 200, c = -200, d = 200;  //область определения
    private int k, m;
    private double[][] grid;
    private BiFunction<Double, Double, Double> function = (x, y) -> x * y;

    public Model(int k, int m)  //k - число значений сетки по x, m - по y
    {
        this.m = m;
        this.k = k;
        grid = new double[m][k];
        calculateGrid();
    }

    private void calculateGrid() {
        for(int i = 0; i < m; i++)
            for(int j = 0; j < k; j++)
            {
                double y = c + i*(d - c)/(m - 1);
                double x = a + j*(b - a)/(k - 1);
                grid[i][j] = function.apply(x, y);
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
}
