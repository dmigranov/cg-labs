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

    public static Matrix add(Matrix m1, Matrix m2)
    {

        return null;
    }

    public static Matrix multiply(Matrix m1, Matrix m2)
    {
        return null;
    }

    public static Matrix multiplyByScalar(double a, Matrix m)
    {
        Matrix n = new Matrix(m.rows, m.cols);
        for(int i = 0; i < m.rows*m.cols; i++)
        {
            n.data[i] = m.data[i] * a;
        }
        return n;
    }

}
