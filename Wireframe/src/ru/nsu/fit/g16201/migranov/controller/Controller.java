package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Figure;
import ru.nsu.fit.g16201.migranov.model.Matrix;
import ru.nsu.fit.g16201.migranov.model.Point2D;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.view.SplinePanel;
import ru.nsu.fit.g16201.migranov.view.WireframePanel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class Controller {
    private static Matrix splineMatrix = Matrix.multiplyByScalar(1.0/6, new Matrix(4, 4, -1, 3, -3, 1, 3, -6, 3, 0, -3, 0, 3, 0, 1, 4, 1, 0));

    private SplinePanel splinePanel;
    private WireframePanel wireframePanel;
    //private double xm, ym;
    //private double scale;

    private Double[] xm, ym;
    private double[] scale;

    private Point3D eye = new Point3D(-10, 0, 0);
    private Point3D ref = new Point3D(10, 0, 0);
    private Point3D up = new Point3D(0, 1, 0);

    private int n, m, k;
    private double a, b, c, d;
    private double zn, zf, sw, sh;  //расстояние до ближней/дальней клиппирующей плоскости; размеры грани объёма визуализации на ближней плоскости

    private Color backgroundColor;
    private Matrix sceneRotateMatrix;
    private Matrix cameraMatrix;
    private Matrix projectionMatrix;

    private List<Figure> figures;
    private Figure currentFigure = null;
    private int currentFigureIndex = 0;

    private List<Point> screenSplinePoints = new ArrayList<>();  //нужен только один, при смене текущего менять; нумерация такой же, как в текущем списке точек модели
    private boolean pointIsGrabbed = false, startedMoving = false;
    private int grabbedPointIndex;

    private Integer prevX = null, prevY = null;

    private int width, height;

    private boolean isFirstTimeDraw;

    public Controller(SplinePanel splinePanel, WireframePanel wireframePanel) {
        this.splinePanel = splinePanel;
        this.wireframePanel = wireframePanel;

        cameraMatrix = Matrix.getViewMatrix(eye, ref, up);  //c 153
        //cameraMatrix = new Matrix(4, 4, 1, 0, 0, +10, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);

        wireframePanel.addMouseWheelListener(e -> {
            int count = e.getWheelRotation();

            if(zf + 0.1*count > zn) {
                zf += 0.1 * count;
                projectionMatrix = Matrix.getProjectionMatrix(sw, sh, zf, zn);
                drawFigures();
            }
        });

        wireframePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                int x = e.getX();
                int y = e.getY();
                if(prevX != null) {
                    int dx = x - prevX;
                    int dy = y - prevY;

                    System.out.println(dx);
                    double xAngle = 0.01 * dx;
                    double yAngle = 0.01 * dy;

                    Matrix xRot = Matrix.getXRotateMatrix(xAngle);
                    Matrix yRot = Matrix.getYRotateMatrix(yAngle);
                }
                prevX = x;
                prevY = y;


            }
        });

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
                                break;
                            }
                        }
                        if (grabbedPoint == null)
                            return;
                        grabbedPointIndex = screenSplinePoints.indexOf(grabbedPoint);
                        //todo: То ли кажется, но иногда новый плохо движется после добавления
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
                    drawSplineLine();
                    drawFigures();
                }
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
        isFirstTimeDraw = true;
        int figureCount;
        currentFigureIndex = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            //scale = 1.1;
            String[] substrings;

            substrings = readLineAndSplit(br);
            n = Integer.parseInt(substrings[0]);
            m = Integer.parseInt(substrings[1]);
            k = Integer.parseInt(substrings[2]);
            if(m <= 0 || n <= 0 || k <= 0)
                throw new IOException("Wrong m, n, or k");
            a = Double.parseDouble(substrings[3]);
            b = Double.parseDouble(substrings[4]);
            if(!(b > a && a >= 0 && 1 >= b))
                throw new IOException("Wrong a or b");
            c = Double.parseDouble(substrings[5]);
            d = Double.parseDouble(substrings[6]);
            if(!(d > c && c >= 0 && 2*Math.PI >= d))
                throw new IOException("Wrong c or d");

            substrings = readLineAndSplit(br);
            zn = Double.parseDouble(substrings[0]);
            zf = Double.parseDouble(substrings[1]);
            sw = Double.parseDouble(substrings[2]);
            sh = Double.parseDouble(substrings[3]);

            if(!(zn > 0 && zf > zn))
                throw new IOException("Wrong clipping");

            projectionMatrix = Matrix.getProjectionMatrix(sw, sh, zf, zn);

            sceneRotateMatrix = read3x3MatrixByRow(br);

            substrings = readLineAndSplit(br);
            backgroundColor = new Color(Integer.parseInt(substrings[0]), Integer.parseInt(substrings[1]), Integer.parseInt(substrings[2]));
            wireframePanel.setBackgroundColor(backgroundColor);
            substrings = readLineAndSplit(br);
            figureCount = Integer.parseInt(substrings[0]);
            figures = new ArrayList<>();

            for (int i = 0; i < figureCount; i++)
            {
                substrings = readLineAndSplit(br);
                Color color = new Color(Integer.parseInt(substrings[0]), Integer.parseInt(substrings[1]), Integer.parseInt(substrings[2]));

                substrings = readLineAndSplit(br);
                Point3D center = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));

                Matrix rotateMatrix = read3x3MatrixByRow(br);

                substrings = readLineAndSplit(br);
                int splinePointCount = Integer.parseInt(substrings[0]);
                if(splinePointCount < 4)
                    throw new IOException("Not enough spline points");
                List<Point2D> splinePoints = new ArrayList<>();
                for(int j = 0; j < splinePointCount; j++)
                {
                    substrings = readLineAndSplit(br);
                    Point2D splinePoint = new Point2D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]));
                    splinePoints.add(splinePoint);
                }
                Figure figure  = new Figure(center, color, rotateMatrix, splinePoints);
                figures.add(figure);
                figure.setModelPoints(new Point3D[n*k + 1][m*k + 1]);

            }
        }
        catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e)
        {
            return -1;
        }

        xm = new Double[figureCount];
        ym = new Double[figureCount];
        scale = new double[figureCount];
        for (int i = 0; i < figureCount; i++)
            scale[i] = 1.1;

        currentFigure = figures.get(0);
        calculateSplineArea();
        drawSplineLine();
        drawFigures();

        return figureCount;
    }

    public void drawFigures() {
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE, minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE, minZ = Double.MAX_VALUE, maxZ = Double.MIN_VALUE;      //куда??!

        wireframePanel.clear();
        for (Figure figure : figures) {
            List<Point2D> splinePoints = figure.getSplinePoints();
            double length = calculateLength(splinePoints), tempLength = 0;
            figure.setLength(length);

            double[] u = new double[n * k + 1];
            Point2D[] Gu = new Point2D[n * k + 1];
            u[0] = a * length;
            for (int i = 1; i < n * k; i++)  //можно и до n+1, но для надежности снизу
            {
                u[i] = u[i - 1] + (b - a) * length / n / k;         //u[i] = u[i-1] + (b - a)*length/n;
            }
            u[n * k] = b * length;
            int uIndex = 0;

            Double xPrev = null, yPrev = null;
            for (int i = 1; i < splinePoints.size() - 2; i++) {
                Matrix Gx = new Matrix(4, 1, splinePoints.get(i - 1).x, splinePoints.get(i).x, splinePoints.get(i + 1).x, splinePoints.get(i + 2).x);
                Matrix Gy = new Matrix(4, 1, splinePoints.get(i - 1).y, splinePoints.get(i).y, splinePoints.get(i + 1).y, splinePoints.get(i + 2).y);
                for (double t = 0; t <= 1; t += 0.01)  //??? я думаю, все таким лучше чтобы было одинаков в подсчете длины и тут, чтобы не было неточности
                {
                    Matrix T = new Matrix(1, 4, t * t * t, t * t, t, 1);
                    Matrix TM = Matrix.multiply(T, splineMatrix);
                    Matrix X = Matrix.multiply(TM, Gx);
                    Matrix Y = Matrix.multiply(TM, Gy);
                    double x = X.get(0, 0), y = Y.get(0, 0);

                    if (xPrev != null)
                        tempLength += Math.sqrt(Math.pow(xPrev - x, 2) + Math.pow(yPrev - y, 2));

                    if (tempLength >= u[uIndex]) {
                        Gu[uIndex] = new Point2D(x, y);
                        uIndex++;
                    }   //todo проверить (не xPrev?)

                    xPrev = x;
                    yPrev = y;
                }
            }

            Point3D[][] modelPoints = figure.getModelPoints();
            Matrix translateMatrix = Matrix.getTranslationMatrix(figure.getCenter());
            Matrix rtm = Matrix.multiply(figure.getRotateMatrix(), translateMatrix);
            for (int i = 0; i < Gu.length; i++) {
                Point2D gu = Gu[i];

                for (int j = 0; j <= m * k; j++) {
                    double v = (d - c) * j / m / k + c;
                    double x = gu.y * Math.cos(v);
                    double y = gu.y * Math.sin(v);
                    double z = gu.x;

                    //todo: по идее в ЭТОМ моменте их надо сложить, а потом в конце самом на результируюзая матрицу умнржить

                    Matrix p = new Matrix(4, 1, x, y, z, 1);
                    //на самом деле произведение r и t имеет простой вид - можно упростить так что

                    Matrix np = Matrix.multiply(rtm, p);
                    double nx = np.get(0, 0), ny = np.get(1, 0), nz = np.get(2, 0);
                    modelPoints[i][j] = new Point3D(nx, ny, nz);

                    if (nx < minX) minX = nx;
                    if (nx > maxX) maxX = nx;
                    if (ny < minY) minY = ny;
                    if (ny > maxY) maxY = ny;
                    if (nz < minZ) minZ = nz;
                    if (nz > maxZ) maxZ = nz;
                }
            }
        //}

        //todo: матрица поворота E! (думаю, её можно внизу)
        //nx = 2 * (x - minX)/(maxx- minx) - 1 и для других - но так не сохр пропорции; поэтому делю на одно и то же
        double maxDim = Math.max(Math.max(maxX - minX, maxY - minY), maxZ - minZ);

        Matrix boxTranslateMatrix = new Matrix(4, 4, 1, 0, 0, -minX,
                                                                        0, 1, 0, -minY,
                                                                        0, 0, 1, -minZ,
                                                                        0, 0, 0, 1);
        /*Matrix boxScaleMatrix = new Matrix(4, 4, 2/maxDim, 0, 0, -1, 0, 2/maxDim, 0, -1, 0, 0, 2/maxDim, -1, 0, 0, 0, 1);*/  //это несимметрично относительно отн нуля
        Matrix boxScaleMatrix = new Matrix(4, 4, 2/maxDim, 0, 0, -(maxX-minX)/maxDim,
                                                                    0, 2/maxDim, 0, -(maxY-minY)/maxDim,
                                                                    0, 0, 2/maxDim, -(maxZ-minZ)/maxDim,
                                                                    0, 0, 0, 1);
        Matrix boxMatrix = Matrix.multiply(boxScaleMatrix, boxTranslateMatrix);
        //Matrix boxMatrix = new Matrix(4, 4, 1, 0, 0,0,0,1,0,0,0,0,1,0,0,0,0,1);

        Matrix projView = Matrix.multiply(cameraMatrix, projectionMatrix);
        Matrix projViewBox = Matrix.multiply(projView, boxMatrix);

        Point3D[][] m0 = figures.get(0).getModelPoints();
        for (int i = 0; i <= n*k; i+=k) {

            for (int j = 0; j <= m * k; j+=k) {
                Point3D p = m0[i][j];
                Matrix pm = new Matrix(4, 1, p.x, p.y, p.z, 1);
                Matrix rpm = Matrix.multiply(projViewBox, pm);
                Point3D rp = new Point3D(rpm.get(0, 0), rpm.get(1, 0), rpm.get(2, 0));
                System.out.println(rp.x + " " + rp.y + " " + rp.z);
            }
        }

        //считаю, что в modelPoints лежат уже отображенные в указанные пределы
        //for (Figure figure : figures)
        //{
            //Point3D[][] modelPoints = figure.getModelPoints();
            Color color = figure.getColor();
            Point[] uPrev = new Point[m*k+1];   //m*k
            for (int i = 0; i <= n*k; i+=k) {
                Point vPrev = null;

                for (int j = 0; j <= m * k; j+=k) {
                    Point3D p = modelPoints[i][j];
                    Matrix mp = new Matrix(4, 1, p.x, p.y, p.z, 1);
                    Matrix nmp = Matrix.multiply(projViewBox, mp);
                    Point3D np = new Point3D(nmp.get(0, 0), nmp.get(1, 0), nmp.get(2, 0));
                    //System.out.println(np.x + " " + np.y + " " + np.z);
                    //todo отсечь и разобраться с z!
                    if(np.x >= -1 && np.x <= 1 && np.y >= -1 && np.y <= 1)
                    {
                        int x = (int)((np.x + 1)/2*wireframePanel.getCanvasWidth());
                        int y = (int)((np.y + 1)/2*wireframePanel.getCanvasHeight());

                        if(vPrev != null)
                        {
                            wireframePanel.drawLine(vPrev.x, vPrev.y, x, y, color);
                            //System.out.println("DRAW M LINE " + vPrev.x + " " + vPrev.y + " " + x + " " + y );
                        }
                        vPrev = new Point(x, y);

                        if(uPrev[j] != null)
                        {
                            wireframePanel.drawLine(uPrev[j].x, uPrev[j].y, x, y, color);
                        }
                        uPrev[j] = new Point(x, y);
                    }
                    else
                    {
                        vPrev = null; //?
                        uPrev[j] = null;
                    }
                }
                /*Point3D p0 = modelPoints[i][0];
                Matrix mp0 = new Matrix(4, 1, p0.x, p0.y, p0.z, 1);
                Point3D p1 = modelPoints[i][n*k];
                Matrix mp1 = new Matrix(4, 1, p1.x, p1.y, p1.z, 1);
                Matrix nmp1 = Matrix.multiply(projViewBox, mp1);
                Point3D np1 = new Point3D(nmp1.get(0, 0), nmp1.get(1, 0), nmp1.get(2, 0));
                Matrix nmp0 = Matrix.multiply(projViewBox, mp1);
                Point3D np0 = new Point3D(nmp1.get(0, 0), nmp1.get(1, 0), nmp1.get(2, 0));
                wireframePanel.drawLine((int)np0.x, (int)np0.y, (int)np1.x, (int)np1.y, color);*/
            }

        }
        wireframePanel.repaint();
    }

    //область определения сплайна (чтобы знать как масштабировать)
    private void calculateSplineArea() {
        if(xm[currentFigureIndex] != null)
            return;

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

        xm[currentFigureIndex] = Math.max(Math.abs(xMax), Math.abs(xMin));
        ym[currentFigureIndex] = Math.max(Math.abs(yMax), Math.abs(yMin));
    }

    private void drawSplineLine() {

        width = splinePanel.getPreferredSize().width;
        height = splinePanel.getPreferredSize().height;

        splinePanel.clear();
        //T - вектор строка t^3 t^2 t 1, t [0,1]
        Figure figure = figures.get(currentFigureIndex);
        List<Point2D> splinePoints = figure.getSplinePoints();

        drawSplinePoints(splinePoints);

        double length = calculateLength(splinePoints);
        Double xPrev = null, yPrev = null;

        Point uv, uvPrev = null;

        double tempLength = 0;

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
                double x = X.get(0, 0), y = Y.get(0, 0);

                uv = getUV(x, y);
                if(uvPrev != null) {
                    if(tempLength > b)
                    {
                        splinePanel.drawLine(uvPrev.x, uvPrev.y, uv.x, uv.y, Color.GRAY);
                        uvPrev = uv;
                        continue;   //нет нужды уже прибавлять
                    }
                    tempLength += Math.sqrt(Math.pow(xPrev - x, 2) + Math.pow(yPrev - y, 2))/length;
                    //if(tempLength >= a && tempLength <= b)
                    if(tempLength >= a)
                        splinePanel.drawLine(uvPrev.x, uvPrev.y, uv.x, uv.y);
                    else
                        splinePanel.drawLine(uvPrev.x, uvPrev.y, uv.x, uv.y, Color.GRAY);
                    //todo: риски?
                }
                uvPrev = uv;
                xPrev = x;
                yPrev = y;
            }
        }

        splinePanel.repaint();
    }

    //тогда можно уж возвращать ещё и точку, где начинается a..
    private double calculateLength(List<Point2D> splinePoints)
    {
        Double xPrev = null, yPrev = null;
        double length = 0;
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

                double x = X.get(0, 0), y = Y.get(0, 0);

                if(xPrev != null) {

                    length += Math.sqrt(Math.pow(xPrev - x, 2) + Math.pow(yPrev - y, 2));
                }
                xPrev = x;
                yPrev = y;
            }
        }
        return length;
    }

    private void drawSplinePoints(List<Point2D> splinePoints) {
        screenSplinePoints.clear();
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
        double xm = this.xm[currentFigureIndex]*scale[currentFigureIndex];    //чтобы оставалось пространство по бокам
        double ym = this.ym[currentFigureIndex]*scale[currentFigureIndex];
        if(xm > ym)
        {
            u = (int)((x + xm)/2/xm * width);
            v = (int)((-y + ym)/2/xm * height + (height - ym*width/xm)/2);  //от 0 до h' < height - непраивльно (смотри картнку) - надо сдвинуть вниз
        }
        else
        {
            v = (int)((-y + ym)/2/ym * height);
            u = (int)((x + xm)/2/ym * width + (width - xm*height/ym)/2);  //от 0 до h' < height - непраивльно (смотри картнку) - надо сдвинуть вниз
        }

        return new Point(u, v);
    }

    private Point2D getXY(int u, int v)
    {
        double x, y;
        double xm = this.xm[currentFigureIndex]*scale[currentFigureIndex];    //чтобы оставалось пространство по бокам
        double ym = this.ym[currentFigureIndex]*scale[currentFigureIndex];
        if(xm > ym)
        {
            x = xm*(2.0*u/width - 1);
            y = -2*xm*v/height - ym*width/height + xm + ym;
        }
        //==? todo
        else
        {
            y = -ym*(2.0*v/height - 1);
            x = (xm*height + 2*ym*u)/width - xm - ym;
        }
        return new Point2D(x, y);
    }

    private Point getUV(Point2D p) {
        return getUV(p.x, p.y);
    }

    //возвращает матрицу 4x4
    private Matrix read3x3MatrixByRow(BufferedReader br) throws IOException {
        String[] substrings;
        Matrix matrix = new Matrix(4, 4);
        for(int i = 0; i < 3; i++)
        {
            substrings = readLineAndSplit(br);
            matrix.setRow(i, new double[] {Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]), 0});
        }
        matrix.setRow(3, new double[] {0, 0, 0, 1});
        return matrix;
    }

    private String[] readLineAndSplit(BufferedReader br) throws IOException
    {
        String line;
        line = br.readLine();
        line = line.substring(0, line.indexOf('/') != -1 ? line.indexOf('/') : line.length());
        return line.split("\\s+");
    }

    public void addSplinePoint(int index) {
        currentFigure.getSplinePoints().add(index, new Point2D(0, 0));
        drawSplineLine();
    }

    public void deleteSplinePoint(int index) {
        if(index >= currentFigure.getSplinePoints().size() || index < 0)
            return;
        currentFigure.getSplinePoints().remove(index);
        drawSplineLine();
    }

    public int getSplinePointsCount() {
        return currentFigure.getSplinePoints().size();
    }


    public void saveFile(File file) {
        try(PrintWriter pw = new PrintWriter(file)) {
            pw.println(n + " " + m + " " + k + " " + a + " " + b + " " + c + " " + d);
            pw.println(zn + " " + zf + " " + sw + " " + sh);

            write3x3MatrixByRow(pw, sceneRotateMatrix);

            pw.println(backgroundColor.getRed() + " " + backgroundColor.getGreen() + " " + backgroundColor.getBlue());
            pw.println(figures.size());

            for(Figure figure : figures)
            {
                Color color = figure.getColor();
                pw.println(color.getRed() + " " + color.getGreen() + " " + color.getBlue());

                Point3D center = figure.getCenter();
                pw.println(center.x + " " + center.y + " " + center.z);

                write3x3MatrixByRow(pw, figure.getRotateMatrix());

                List<Point2D> splinePoints = figure.getSplinePoints();
                pw.println(splinePoints.size());

                for(Point2D p : splinePoints)
                {
                    pw.println(p.x + " " + p.y);
                }
            }
        }
        catch(IOException e)
        {
        }
    }

    //4 строку и столбец - выбрасываем
    private void write3x3MatrixByRow(PrintWriter pw, Matrix matrix) {
        for(int i = 0; i < 3; i++)
        {
            String s;
            //s = String.format("%f %f %f", matrix.get(i, 0), matrix.get(i, 1), matrix.get(i, 2));
            s = matrix.get(i, 0) + " " + matrix.get(i, 1) + " " +  matrix.get(i, 2);
            pw.println(s);
        }
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int getK() {
        return k;
    }

    public void setConstants(int n, int m, int k, double a, double b, double c, double d) {
        this.n = n;
        this.m = m;
        this.k = k;

        for(Figure figure : figures)
            figure.setModelPoints(new Point3D[n*k + 1][m*k + 1]);

        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;

        drawSplineLine();
        drawFigures();
    }

    public void changeScale(double ds) {
        if(this.scale[currentFigureIndex] + ds > 0)
            this.scale[currentFigureIndex] += ds;
        drawSplineLine();
    }

    public void setCurrentFigure(int index) {
        //todo: Проверка?

        //масштаб при изначальной загрузке такой, чтобы всё было красиво вписано (он не одинаковый для разных вкладок!)
        currentFigureIndex = index;
        currentFigure = figures.get(index);
        calculateSplineArea();
        drawSplineLine();
    }

    public void setAB(double a, double b) {
        this.a = a;
        this.b = b;

        drawSplineLine();
        //todo: пересчитать 3d
    }

    public double getSw() {
        return sw;
    }

    public void setSw(double sw) {
        this.sw = sw;
    }

    public double getSh() {
        return sh;
    }

    public void setSh(double sh) {
        this.sh = sh;
    }
}
