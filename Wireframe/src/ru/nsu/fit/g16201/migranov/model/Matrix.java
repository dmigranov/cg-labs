package ru.nsu.fit.g16201.migranov.model;

import java.util.Arrays;

public class Matrix {
    private double[] data;

    private int rows, cols;
    public Matrix(int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
        data = new double[rows * cols];
    }

    public Matrix(int rows, int cols, double ... elements)
    {
        if(elements.length != rows * cols)
            throw new IllegalArgumentException();
        this.rows = rows;
        this.cols = cols;
        data = new double[rows * cols];
        //todo
    }

    public void setRow(int rowNumber, double[] row)
    {
        if(row.length != cols || rowNumber >= rows)
            throw new IllegalArgumentException();
        System.arraycopy(row, 0, data, rowNumber*cols, row.length);
    }

    public static void add(Matrix m1, Matrix m2)
    {

    }

}
