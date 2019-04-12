package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Figure;
import ru.nsu.fit.g16201.migranov.model.Matrix;
import ru.nsu.fit.g16201.migranov.model.Point2D;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.view.SplinePanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Controller {
    private static Matrix splineMatrix = Matrix.multiplyByScalar(1.0/6, new Matrix(4, 4, -1, 3, -3, 1, 3, -6, 3, 0, -3, 0, 3, 0, 1, 4, 1, 0));


    private SplinePanel splinePanel;
    private double xm, ym;


    private int n, m, k;
    private double a, b, c, d;
    private double zn, zf, sw, sh;  //расстояние до ближней/дальней клиппирующей плоскости; размеры грани объёма визуализации на ближней плоскости
    private Color backgroundColor;
    private Matrix sceneRotateMatrix;
    private List<Figure> figures;
    private Figure currentFigure = null;
    private int currentFigureIndex = 0;


    private BufferedReader br;

    //private Map<Point, Point2D> pointsMap = new HashMap<>();    //todo: при добавлении/изменении надо обновлять
    //private Map<Point, Integer> pointsMap = new HashMap<>();    //Integer - номер в листе
    private List<Point> screenSplinePoints = new ArrayList<>();  //нужен только один, при смене текущего менять; нумерация такой же, как в текущем списке точек модели
    private boolean pointIsGrabbed = false, startedMoving = false;
    private int grabbedPointIndex;


    private int width, height;


    public Controller(SplinePanel splinePanel) {
        this.splinePanel = splinePanel;
        splinePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                if(startedMoving && !pointIsGrabbed)
                    return;
                startedMoving = true;

                int x = e.getX(), y = e.getY();

                if(!pointIsGrabbed) {
                    if (splinePanel.getRGB(x, y) == splinePanel.getSplinePointColor()) {
                        int radius = splinePanel.getSplinePointRadius();
                        Point grabbedPoint = null;
                        for (Point p : screenSplinePoints) {
                            if (Math.abs(p.x - x) <= radius && Math.abs(p.y - y) <= radius) {
                                pointIsGrabbed = true;
                                grabbedPoint = p;
                                break;                             //todo: искать не первый, а наиболее близкий
                            }
                        }
                        if (grabbedPoint == null)
                            return;
                        grabbedPointIndex = screenSplinePoints.indexOf(grabbedPoint);

                    }
                }
                if(pointIsGrabbed)
                {
                    if(x < 0)
                        x = 0;
                    else if(x >= width)
                        x = width -1 ;
                    if(y < 0)
                        y = 0;
                    else if(y >= height)
                        y = height -1 ;

                    //я не трогаю mx и my, чтобы не менять масштаб
                    Point2D movedPoint = currentFigure.getSplinePoints().get(grabbedPointIndex);
                    Point2D newCoords = getXY(x, y);
                    movedPoint.x = newCoords.x;
                    movedPoint.y = newCoords.y;
                    screenSplinePoints.get(grabbedPointIndex).x = x;
                    screenSplinePoints.get(grabbedPointIndex).y = y;
                    drawSplineLine();
                }

                //при изменении положения удалять из списка по индексу и вставлять по индексу новый (а когда-то может стоит пересчитывать? при открытии окошка например)
            }
        });

        splinePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                startedMoving = false;
                pointIsGrabbed = false;
            }
        });
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

        currentFigure = figures.get(0);
        calculateSplineArea();
        drawSplineLine();

        return 0;
    }

    //область определения сплайна (чтобы знать как масштабировать)
    private void calculateSplineArea() {
        double xMin = Double.MAX_VALUE, xMax = Double.MIN_VALUE, yMin = Double.MAX_VALUE, yMax = Double.MIN_VALUE;

        Figure figure = figures.get(currentFigureIndex);
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
    }

    private void drawSplineLine() {

        width = splinePanel.getPreferredSize().width;
        height = splinePanel.getPreferredSize().height;

        splinePanel.clear();
        //T - вектор строка t^3 t^2 t 1, t [0,1]
        Figure figure = figures.get(currentFigureIndex); //todo итерация по телам
        List<Point2D> splinePoints = figure.getSplinePoints();

        drawSplinePoints(splinePoints);

        Point uv, prev = null;
        for(int i = 1; i < splinePoints.size() - 2; i++)
        {
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

                //splinePanel.drawPoint(uv.x, uv.y);
                if(prev != null)
                    splinePanel.drawLine(prev.x, prev.y, uv.x, uv.y);
                prev = uv;
            }
        }

        splinePanel.repaint();

    }

    private void drawSplinePoints(List<Point2D> splinePoints) {
        for(Point2D p : splinePoints)
        {
            Point uv = getUV(p);
            splinePanel.drawSplinePoint(uv.x, uv.y);
            screenSplinePoints.add(uv);   //может куда-то ещё ложить номер, чтобы легко найти
        }
    }

    private Point getUV(double x, double y) {
        //width = height!
        int u, v;
        double xm = this.xm*1.1;    //чтобы оставалось пространство по бокам
        double ym = this.ym*1.1;
        if(xm > ym)
        {
            u = (int)((x + xm)/2/xm * width);
            v = (int)((-y + ym)/2/xm * height + (height - ym*width/xm)/2);  //от 0 до h' < height - непраивльно (смотри картнку) - надо сдвинуть вниз
        }
        //==?
        else
        {
            //todo проверить
            v = (int)((-y + ym)/2/ym * height);
            u = (int)((x + xm)/2/ym * width + (width - xm*height/ym)/2);  //от 0 до h' < height - непраивльно (смотри картнку) - надо сдвинуть вниз
        }


        return new Point(u, v);
    }

    private Point2D getXY(int u, int v)
    {
        double x = 0, y = 0;
        double xm = this.xm*1.1;    //чтобы оставалось пространство по бокам
        double ym = this.ym*1.1;
        if(xm > ym)
        {
            x = xm*(2.0*u/width - 1);
            //y = -(2*(v-height)*xm/height + ym*width/height - ym);
            y = -2*xm*v/height - ym*width/height + xm + ym;
        }
        //==?
        else
        {
            //todo написать
            //v = (int)((-y + ym)/2/ym * height);
            //u = (int)((x + xm)/2/ym * width + (width - xm*height/ym)/2);  //от 0 до h' < height - непраивльно (смотри картнку) - надо сдвинуть вниз
        }
        return new Point2D(x, y);
    }

    private Point getUV(Point2D p) {
        return getUV(p.x, p.y);
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
