package ru.nsu.fit.g16201.migranov.model;

public class Matrix {
    private double[] array;

    private double rows, cols;
    public Matrix(int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
        array = new double[rows * cols];
    }

    public Matrix(int rows, int cols, double ... elements)
    {
        if(elements.length != rows * cols)
            throw new IllegalArgumentException();
        this.rows = rows;
        this.cols = cols;
        array = new double[rows * cols];
        //todo
    }

    public void setRow(double[] row)
    {
        if(row.length != cols)
            throw new IllegalArgumentException();

    }

    public static void add(Matrix m1, Matrix m2)
    {

    }

}
