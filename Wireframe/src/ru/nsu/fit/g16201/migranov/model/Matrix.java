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

        data = elements; //на самом деле нехорошо - а вдруг изменят значение (теоретически можно подать прям массив)?

        //data = new double[rows * cols];
        //for(int i = 0; )

    }

    public static Matrix getTranslationMatrix(Point3D where) {
        return new Matrix(4, 4,
                1, 0, 0, where.x,
                        0, 1, 0, where.y,
                        0, 0, 1, where.z,
                        0, 0, 0, 1);
    }

    public static Matrix getViewMatrix(Point3D eye, Point3D ref, Point3D up) {
        Point3D w = new Point3D(eye.x - ref.x, eye.y - ref.y, eye.z - ref.z).normalize();
        Point3D rr = Point3D.getVectorProduct(up, w);
        Point3D u = rr.normalize();
        Point3D v = Point3D.getVectorProduct(w, u);

        return new Matrix(4, 4
        );
    }

    public void setRow(int rowNumber, double[] row)
    {
        if(row.length != cols || rowNumber >= rows)
            throw new IllegalArgumentException();
        System.arraycopy(row, 0, data, rowNumber*cols, row.length);
    }


    public static Matrix add(Matrix M1, Matrix M2)
    {
        double[] m1d = M1.data;
        double[] m2d = M2.data;
        if(m1d.length != m2d.length || M1.cols != M2.cols)
            throw new IllegalArgumentException();

        Matrix N = new Matrix(M1.rows, M1.cols);

        double[] nd = N.data;
        for(int i = 0; i < m1d.length; i++)
        {
            nd[i] = m1d[i] + m2d[i];
        }
        return N;
    }

    public static Matrix multiply(Matrix M1, Matrix M2)
    {
        //todo: проверка?
        Matrix N = new Matrix(M1.rows, M2.cols);
        double[] m1d = M1.data;
        double[] m2d = M2.data;
        double[] nd = N.data;

        int l = M1.rows;
        int m = M1.cols; //=m2.rows
        int n = M2.cols;

        for(int i = 0; i < l; i++)
            for(int j = 0; j < n;j++)    //m2.rows = m1.cols
                for(int k = 0; k < m; k++)
                    nd[i * n + j] += m1d[i * m + k] * m2d[k * n + j];

        return N;
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


    public double get(int row, int col) {
        return data[row * cols + col];
    }
}
