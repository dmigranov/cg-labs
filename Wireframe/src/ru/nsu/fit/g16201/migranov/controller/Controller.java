package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Figure;
import ru.nsu.fit.g16201.migranov.model.Matrix;
import ru.nsu.fit.g16201.migranov.model.Point2D;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.view.SplinePanel;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Controller {
    private static Matrix splineMatrix = Matrix.multiplyByScalar(1.0/6, new Matrix(4, 4, -1, 3, -3, 1, 3, -6, 3, 0, -3, 0, 3, 0, 1, 4, 1, 0));


    private SplinePanel splinePanel;
    private double xm, ym;

    private int currentFigure = 0;

    private int n, m, k;
    private double a, b, c, d;
    private double zn, zf, sw, sh;  //расстояние до ближней/дальней клиппирующей плоскости; размеры грани объёма визуализации на ближней плоскости
    private Color backgroundColor;
    private Matrix sceneRotateMatrix;
    private List<Figure> figures;

    private int width, height;
    private BufferedReader br;

    public Controller(SplinePanel splinePanel) {
        this.splinePanel = splinePanel;
    }

    public int loadFile(File file) {
        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            this.br = br;
            String[] substrings;

            substrings = readLineAndSplit();
            n = Integer.parseInt(substrings[0]);
            m = Integer.parseInt(substrings[1]);
            k = Integer.parseInt(substrings[2]);
            a = Double.parseDouble(substrings[3]);
            b = Double.parseDouble(substrings[4]);
            c = Double.parseDouble(substrings[5]);
            d = Double.parseDouble(substrings[6]);

            substrings = readLineAndSplit();
            zn = Double.parseDouble(substrings[0]);
            zf = Double.parseDouble(substrings[1]);
            sw = Double.parseDouble(substrings[2]);
            sh = Double.parseDouble(substrings[3]);

            sceneRotateMatrix = read3x3MatrixByRow();

            substrings = readLineAndSplit();
            backgroundColor = new Color(Integer.parseInt(substrings[0]), Integer.parseInt(substrings[1]), Integer.parseInt(substrings[2]));

            int figureCount;
            substrings = readLineAndSplit();
            figureCount = Integer.parseInt(substrings[0]);
            figures = new ArrayList<>();

            for (int i = 0; i < figureCount; i++)
            {
                substrings = readLineAndSplit();
                Color color = new Color(Integer.parseInt(substrings[0]), Integer.parseInt(substrings[1]), Integer.parseInt(substrings[2]));

                substrings = readLineAndSplit();
                Point3D center = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));

                Matrix rotateMatrix = read3x3MatrixByRow();

                substrings = readLineAndSplit();
                int splinePointCount = Integer.parseInt(substrings[0]);
                List<Point2D> splinePoints = new ArrayList<>();
                for(int j = 0; j < splinePointCount; j++)
                {
                    substrings = readLineAndSplit();
                    Point2D splinePoint = new Point2D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]));
                    splinePoints.add(splinePoint);
                }
                Figure figure  = new Figure(center, color, rotateMatrix, splinePoints);
                figures.add(figure);
            }
        }
        catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e)
        {
            return -1;
        }


        calculateSplineArea();
        drawSplineLine();


        return 0;
    }

    //область определения сплайна (чтобы знать как масштабировать)
    private void calculateSplineArea() {
        double xMin = Double.MAX_VALUE, xMax = Double.MIN_VALUE, yMin = Double.MAX_VALUE, yMax = Double.MIN_VALUE;

        Figure figure = figures.get(currentFigure);
        List<Point2D> splinePoints = figure.getSplinePoints();

        for(Point2D p : splinePoints)
        {
            xMax = p.x > xMax ? p.x : xMax;
            xMin = p.x < xMin ? p.x : xMin;
            yMax = p.y > yMax ? p.y : yMax;
            yMin = p.y < yMin ? p.y : yMin;
        }

        xm = Math.max(Math.abs(xMax), Math.abs(xMin));
        ym = Math.max(Math.abs(yMax), Math.abs(yMin));
        System.out.println(xm + " " + ym);
    }

    private void drawSplineLine() {

        width = splinePanel.getPreferredSize().width;
        width-=width/20;
        height = splinePanel.getPreferredSize().height;
        height-=height/20;

        //T - вектор строка t^3 t^2 t 1, t [0,1]
        Figure figure = figures.get(currentFigure); //todo итерация по телам
        List<Point2D> splinePoints = figure.getSplinePoints();
        //long start = System.currentTimeMillis();
        int uv[] = getUV(splinePoints.get(0).x, splinePoints.get(0).y);
        splinePanel.drawSplinePoint(uv[0], uv[1]);
        for(int i = 1; i < splinePoints.size() - 2; i++)
        {

            uv = getUV(splinePoints.get(i).x, splinePoints.get(i).y);
            splinePanel.drawSplinePoint(uv[0], uv[1]);

            //splinePanel.drawSplinePoint();
            Matrix Gx = new Matrix(4, 1, splinePoints.get(i - 1).x, splinePoints.get(i).x, splinePoints.get(i + 1).x, splinePoints.get(i + 2).x);
            Matrix Gy = new Matrix(4, 1, splinePoints.get(i - 1).y, splinePoints.get(i).y, splinePoints.get(i + 1).y, splinePoints.get(i + 2).y);
            for(double t = 0; t <= 1; t+=0.01)
            {
                Matrix T = new Matrix(1, 4, t*t*t, t*t, t, 1);
                Matrix TM = Matrix.multiply(T, splineMatrix);

                Matrix X = Matrix.multiply(TM, Gx);
                Matrix Y = Matrix.multiply(TM, Gy);

                double x = X.get(0, 0);
                double y = Y.get(0, 0);

                uv = getUV(x, y);

                //todo: лучше наверное рисовать линию между предыдущей и нынешней чтобы в слкчае чего не было точек
                splinePanel.drawPoint(uv[0], uv[1]);
            }
        }
        splinePanel.repaint();

    }

    private int[] getUV(double x, double y) {
        //точно все праивльно? не порчу ли я чего из-за деления на разное? подумать
        double max = Math.max(width/xm, height/ym);
        //todo: отзеркалить относительно игрека

        return new int[]{ (int)((x + xm)/2*max), (int)((y + ym)/2*max)};
    }

    //возвращает матрицу 4x4
    private Matrix read3x3MatrixByRow() throws IOException {
        String[] substrings;
        Matrix matrix = new Matrix(4, 4);
        for(int i = 0; i < 3; i++)
        {
            substrings = readLineAndSplit();
            matrix.setRow(i, new double[] {Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]), 0});
        }
        matrix.setRow(3, new double[] {0, 0, 0, 1});

        return matrix;
    }


    private String[] readLineAndSplit() throws IOException
    {
        String line;
        line = br.readLine();
        line = line.substring(0, line.indexOf('/') != -1 ? line.indexOf('/') : line.length());
        return line.split("\\s+");
    }
}
