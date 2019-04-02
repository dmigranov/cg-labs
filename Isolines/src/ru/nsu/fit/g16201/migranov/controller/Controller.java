package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Model;
import ru.nsu.fit.g16201.migranov.view.LegendPanel;
import ru.nsu.fit.g16201.migranov.view.MapPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static ru.nsu.fit.g16201.migranov.controller.Seed.*;

public class Controller {
    public static final int INTERPOLATION = 0;
    public static final int SPAN = 1;
    public static final int PERPIXELACTUAL = 2;
    private int mode = SPAN;

    private final MapPanel mapPanel;
    private final LegendPanel legendPanel;
    private BufferedReader br;
    private List<Color> legendColors;
    private Color isolineColor;
    private Model mapModel, legendModel;
    private int n;      //количество цветов (на самом деле уменьшенное на единицу, т.к. c0, c1, ..., cn)

    private boolean isGridEnabled = false;
    private boolean areIsolinesEnabled = true;
    private boolean areGridPointsEnabled = false;
    //private boolean interpolationEnabled = false;
    //private boolean perPixelColorMapEnabled = false;
    //private List<Point2D> mapLines, userLines;        //l1p1 l1p2 l2p1 l2p2
    //private Set<Seed> mapSeeds, legendSeeds;
    private List<Line> mapLines, userLines, dynamicUserLine;
    private List<Seed> mapSeeds;


    public Controller(MapPanel mapPanel, LegendPanel legendPanel, JLabel statusLabel) {
        this.mapPanel = mapPanel;
        this.legendPanel = legendPanel;

        mapPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e)
            {
                if(mapModel != null && legendModel != null) {
                    mapPanel.updateSize();
                    legendPanel.updateSize();
                    drawLegend();
                    drawMap();
                    recalculateAndDrawUserLines();
                    mapPanel.repaint();
                    legendPanel.repaint();
                }
            }
        });

        mapPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                int x = e.getX(), y = e.getY();
                if(x < 0 || x > mapPanel.getWidth() || y < 0 || y > mapPanel.getHeight() || mapModel == null)
                    return;
                double mx = ((mapModel.getB() - mapModel.getA()) * x/mapPanel.getWidth() + mapModel.getA());
                double my = ((mapModel.getD() - mapModel.getC()) * y/mapPanel.getHeight() + mapModel.getC());
                double f = mapModel.applyFunction(mx, my);

                statusLabel.setText(String.format("x = %.3f", mx) + ", " + String.format("y = %.3f", my) + "; " + String.format("f(x, y) = %.3f", f));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                int x = e.getX(), y = e.getY();
                if(x < 0 || x > mapPanel.getWidth() || y < 0 || y > mapPanel.getHeight() || mapModel == null)
                    return;

                mapPanel.clearUserLine();
                dynamicUserLine.clear();
                calculateMapForLevel(mapModel, dynamicUserLine, null, interpolate(x, y), 0);
                recalculateAndDrawUserLines();

                mapPanel.repaint();

            }
        });
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                userLines.addAll(dynamicUserLine);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //mapPanel.clearUserLine();
                int x = e.getX(), y = e.getY();
                if (x < 0 || x > mapPanel.getWidth() || y < 0 || y > mapPanel.getHeight() || mapModel == null)
                    return;

                /*double mx = ((mapModel.getB() - mapModel.getA()) * x / mapPanel.getWidth() + mapModel.getA());
                double my = ((mapModel.getD() - mapModel.getC()) * y / mapPanel.getHeight() + mapModel.getC());
                double z = mapModel.applyFunction(mx, my);  //точное значение функции*/

                calculateMapForLevel(mapModel, userLines, null, interpolate(x, y), 0);
                recalculateAndDrawUserLines();

                mapPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                statusLabel.setText("");
            }
        });
    }

    private double interpolate(int u, int v)
    {

        double a = mapModel.getA();
        double b = mapModel.getB();
        double c = mapModel.getC();
        double d = mapModel.getD();

        int width = mapPanel.getWidth();
        int height = mapPanel.getHeight();

        double wk = (double)width / (mapModel.getK() - 1); //размер ячейки
        double vm = (double)height / (mapModel.getM() - 1);
        int j = (int)(u / wk);
        if (j >= mapModel.getK() - 1)
            j = mapModel.getK() - 2;
        if (j < 0)
            j = 0;
        int i = (int)(v / vm);
        if (i >= mapModel.getM() - 1)
            i = mapModel.getM() - 2;
        if (i < 0)
            i = 0;
        double uModel = (b - a) * u / width + a;
        double vModel = (d - c) * v / height + c;

        Point2D f3p = mapModel.getPoint(j, i );
        Point2D f2p = mapModel.getPoint(j+1, i + 1);
        double u0 = f3p.getX();
        double u1 = f2p.getX();

        double v0 = f3p.getY();
        double v1 = f2p.getY();

        double f1=0, f2=0, f3=0, f4=0;
        try {
            f1 = mapModel.getValue(j, i + 1);
            f2 = mapModel.getValue(j + 1, i + 1);
            f3 = mapModel.getValue(j, i);
            f4 = mapModel.getValue(j + 1, i);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
        }

        double ccx0 = f1 * (u1 - uModel)/(u1 - u0) + f2 * (uModel - u0)/(u1-u0);       //по верхнему ребру
        double ccx1 = f3 * (u1 - uModel)/(u1 - u0) + f4 * (uModel - u0)/(u1-u0);       //по нижнему ребру

        return ccx1 * (v1 - vModel)/(v1 - v0) + ccx0 * (vModel - v0)/(v1 - v0);
    }

    private void recalculateAndDrawUserLines() {
        int width = mapPanel.getWidth(), height = mapPanel.getHeight();
        double a = mapModel.getA(), b = mapModel.getB(), c = mapModel.getC(), d = mapModel.getD();

        for(Line l : userLines)
        {
            Point2D p1 = l.p1;
            Point2D p2 = l.p2;
            double x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2  = p2.getY();
            int u1 = (int)(width * (x1 - a)/(b - a) + 0.5);
            int u2 = (int)(width * (x2 - a)/(b - a) + 0.5);
            int v1 = (int)(height * (y1 - c)/(d - c) + 0.5);
            int v2 = (int)(height * (y2 - c)/(d - c) + 0.5);
            /*if (areGridPointsEnabled) {
                mapPanel.drawGridPoint(u1, v1);
                mapPanel.drawGridPoint(u2, v2);
            }*/
            mapPanel.drawUserLine(u1, v1, u2, v2);
        }
        for(Line l : dynamicUserLine)
        {
            Point2D p1 = l.p1;
            Point2D p2 = l.p2;
            double x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2  = p2.getY();
            int u1 = (int)(width * (x1 - a)/(b - a) + 0.5);
            int u2 = (int)(width * (x2 - a)/(b - a) + 0.5);
            int v1 = (int)(height * (y1 - c)/(d - c) + 0.5);
            int v2 = (int)(height * (y2 - c)/(d - c) + 0.5);
            /*if (areGridPointsEnabled) {
                mapPanel.drawGridPoint(u1, v1);
                mapPanel.drawGridPoint(u2, v2);
            }*/
            mapPanel.drawUserLine(u1, v1, u2, v2);
        }
    }

    public int loadFile(File file) {

        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            this.br = br;
            String[] substrings;

            substrings = readLineAndSplit();
            int k = Integer.parseInt(substrings[0]);    //число значений сетки по x
            int m = Integer.parseInt(substrings[1]);    //число значений сетки по y
            if(k <= 0 || m <= 0)
                throw new NumberFormatException();
            mapModel = new Model(k, m);

            substrings = readLineAndSplit();
            int n = Integer.parseInt(substrings[0]);    //число уровней
            this.n = n;
            n++;    //т.к. от 0 до n
            legendColors = new ArrayList<>();
            while (n > 0)
            {
                substrings = readLineAndSplit();
                int r = Integer.parseInt(substrings[0]);
                int g = Integer.parseInt(substrings[1]);
                int b = Integer.parseInt(substrings[2]);
                if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
                    throw new NumberFormatException("Wrong colors");
                legendColors.add(new Color(r, g, b));
                n--;
            }
            substrings = readLineAndSplit();
            int r = Integer.parseInt(substrings[0]);
            int g = Integer.parseInt(substrings[1]);
            int b = Integer.parseInt(substrings[2]);
            isolineColor = new Color(r, g, b);

            mapPanel.updateSize();
            legendPanel.updateSize();

            legendModel = new Model(this.n + 2, 2, (x,y)->x, 0, 1, 0, 1);

            mapPanel.clear();
            legendPanel.getLegendMap().clear();
            mapPanel.clearGrid();
            legendPanel.getLegendMap().clearGrid();
            mapPanel.setColor(isolineColor);
            legendPanel.getLegendMap().setColor(isolineColor);
            mapLines = new ArrayList<>();
            //mapColorLines = new ArrayList<>();
            mapSeeds = new ArrayList<>();
            userLines = new ArrayList<>();
            dynamicUserLine = new ArrayList<>();
            drawLegend();
            calculateMap(mapModel, mapLines, mapSeeds);
            drawMap();
            mapPanel.repaint();
            legendPanel.repaint();
        }
        catch (IOException e)
        {
            return -1;
        }
        return 0;
    }

    private String[] readLineAndSplit() throws IOException
    {
        String line;
        line = br.readLine();
        line = line.substring(0, line.indexOf('/') != -1 ? line.indexOf('/') : line.length());
        return line.split("\\s+");
    }

    private void drawMap() {
        recalculateAndDrawMap(mapPanel, mapModel, mapLines, mapSeeds);
        if(isGridEnabled) {
            drawGrid(mapPanel, mapModel);
        }
    }

    private void drawLegend() {
        //recalculateAndDrawMap(legendPanel.getLegendMap(), legendModel, legendLines, legendSeeds);
        MapPanel legendMap = legendPanel.getLegendMap();
        if(mode != INTERPOLATION)
        {
            for (int j = 0; j <= n; j++) {
                int x = (int) Math.round(legendMap.getWidth() * legendModel.getValue(j, 0));
                legendMap.drawLine(x, 0, x, legendMap.getHeight());
                legendMap.spanFill(1 + x, 1, legendColors.get(j).getRGB());
            }
        }
        else {
            double wk = (double) mapPanel.getWidth() / (n + 1); //размер ячейки

            double[] colors = new double[n + 1];
            for (int i = 0; i <= n; i++) {
                colors[i] = wk * i + wk / 2;
            }
            for (int u = 0; u < legendMap.getWidth(); u++) {
                if (u <= colors[0]) {
                    legendMap.drawLineInterpolated(u, 0, u, legendMap.getHeight(), legendColors.get(0).getRGB());
                    continue;
                }
                if (u >= colors[n]) {
                    legendMap.drawLineInterpolated(u, 0, u, legendMap.getHeight(), legendColors.get(n).getRGB());
                    continue;
                }
                int i;
                for (i = 0; i < n; i++) {
                    if (u >= colors[i] && u < colors[i + 1])
                        break;
                }
                double u0 = colors[i];
                double u1 = colors[i + 1];

                int c0 = legendColors.get(i).getRGB();
                int c1 = legendColors.get(i + 1).getRGB();
                int newColor = 0;
                for (int k = 0; k < 24; k += 8) {
                    int cc0 = c0 >> k & 0x000000FF;
                    int cc1 = c1 >> k & 0x000000FF;
                    int ccxx = (int) (cc0 * (u1 - u) / (u1 - u0) + cc1 * (u - u0) / (u1 - u0));
                    if (ccxx < 0)
                        ccxx = 0;
                    if (ccxx > 255)
                        ccxx = 255;
                    newColor |= (ccxx << k);
                }
                legendMap.drawLineInterpolated(u, 0, u, legendMap.getHeight(), newColor);
            }
        }

        if(isGridEnabled) {
            drawGrid(legendPanel.getLegendMap(), legendModel);
        }
        legendPanel.drawText(n, mapModel.getMinValue(), mapModel.getMaxValue(), legendModel);
    }


    //чтобы каждый раз не считать изолинии, сохранять их в лист и при ресайзе переводить из системы xy в uv
    private void recalculateAndDrawMap(MapPanel mapPanel, Model model, List<Line> lines, List<Seed> seeds)
    {
        int width = mapPanel.getWidth(), height = mapPanel.getHeight();
        double a = model.getA(), b = model.getB(), c = model.getC(), d = model.getD();

        //for (int i = 0; i < lines.size(); i += 2) {
            //Point2D p1 = lines.get(i);
            //Point2D p2 = lines.get(i + 1);

        for(Line l : lines)
        {
            Point2D p1 = l.p1;
            Point2D p2 = l.p2;
            Color color = l.color;

            double x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2 = p2.getY();
            int u1 = (int) (width * (x1 - a) / (b - a) + 0.5);
            int u2 = (int) (width * (x2 - a) / (b - a) + 0.5);
            int v1 = (int) (height * (y1 - c) / (d - c) + 0.5);
            int v2 = (int) (height * (y2 - c) / (d - c) + 0.5);

            mapPanel.drawLine(u1, v1, u2, v2);
            mapPanel.drawColorLine(u1, v1, u2, v2, color);
            if (areGridPointsEnabled) {
                mapPanel.drawGridPoint(u1, v1);
                mapPanel.drawGridPoint(u2, v2);
            }
        }
        if(mode == PERPIXELACTUAL) {
            for (int v = 0; v < height; v++) {
                double y = (d - c) * v / height + c;
                for (int u = 0; u < width; u++) {
                    double x = (b - a) * u / width + a;

                    double f = mapModel.applyFunction(x, y);
                    int l;
                    double zOld = model.getMinValue();
                    for (l = 1; l <= n; l++) {
                        if (f < model.getMinValue())
                            break;
                        double z = model.getMinValue() + l * (model.getMaxValue() - model.getMinValue()) / (n + 1);

                        if (f < z && f >= zOld) {
                            break;
                        }
                    }
                    mapPanel.paintPixel(u, v, legendColors.get(l - 1).getRGB());
                }
            }
        }
        else if (mode == SPAN)
        {
            for (Seed s : seeds) {
                int direction = s.direction;
                double x = s.x, y = s.y;
                int color = s.color;

                int us = (int) (width * (x - a) / (b - a) + 0.5);
                int vs = (int) (height * (y - c) / (d - c) + 0.5);

                if((direction & UP) == UP)
                    vs+=2;  //на самом деле вниз
                else if((direction & DOWN) == DOWN)
                    vs-=2;
                else if((direction & RIGHT) == RIGHT)
                    us+=2;
                else if((direction & LEFT) == LEFT)
                    us-=2;

                if(us < 0)
                    us = 0;
                else if (us >= width)
                    us = width - 1;

                if(vs < 0)
                    vs = 0;
                else if (vs >= height)
                    vs = height - 1;


                mapPanel.spanFill(us, vs, color);
                //mapPanel.drawGridPoint(us, vs, color);    //for test only
            }
        }
        else if(mode == INTERPOLATION)    //else if интерполяция
        {
            //можно заменить на вызов функции interpolate в цикле, Но боюсь за скорость
            double wk = (double)mapPanel.getWidth() / (model.getK() - 1); //размер ячейки
            double vm = (double)mapPanel.getHeight() / (model.getM() - 1);
            for(int v = 0; v < mapPanel.getHeight(); v++) {
                for (int u = 0; u < mapPanel.getWidth(); u++) {
                    int j =  (int)(u / wk);
                    int i =  (int)(v / vm);

                    double uModel = (b - a) * u / width + a;
                    double vModel = (d - c) * v / height + c;

                    Point2D f3p = model.getPoint(j, i );
                    Point2D f2p = model.getPoint(j+1, i + 1);
                    double u0 = f3p.getX();
                    double u1 = f2p.getX();

                    double v0 = f3p.getY();
                    double v1 = f2p.getY();

                    double f1 = model.getValue(j, i + 1);
                    double f2 = model.getValue(j + 1, i + 1);
                    double f3 = model.getValue(j, i);
                    double f4 = model.getValue(j + 1, i);

                    double ccx0 = f1 * (u1 - uModel)/(u1 - u0) + f2 * (uModel - u0)/(u1-u0);       //по верхнему ребру
                    double ccx1 = f3 * (u1 - uModel)/(u1 - u0) + f4 * (uModel - u0)/(u1-u0);       //по нижнему ребру

                    double ccxx = ccx1 * (v1 - vModel)/(v1 - v0) + ccx0 * (vModel - v0)/(v1 - v0);

                    int legendX = (int)((ccxx - model.getMinValue())/ (model.getMaxValue() - model.getMinValue()) * width);

                    if(legendX >= width)
                        legendX = width -1;

                    mapPanel.paintPixelInterpolated(u, v, legendPanel.getColorInterpolated(legendX, 1));
                }
            }
        }
    }

    //первый раз только при загрузке файла. в пространстве xy
    private void calculateMap(Model model, List<Line> lines, List<Seed> seeds)
    {
        for(int l = 1; l <= n; l++)     //так?
        {
            double z = model.getMinValue() + l * (model.getMaxValue() - model.getMinValue()) / (n + 1);
            calculateMapForLevel(model, lines, seeds, z, l);
        }
    }

    private void calculateMapForLevel(Model model, List<Line> lines, List<Seed> seeds, double z, int l) {

        Color lesserColor, biggerColor;
        if(l > 0) {
            lesserColor = legendColors.get(l - 1);
            biggerColor = legendColors.get(l);
        }
        else
        {
            biggerColor = Color.BLACK;
            lesserColor = Color.BLACK;
        }
        double a=model.getA(), b = model.getB(), c=model.getC(), d = model.getD();

        for (int i = 0; i < model.getM() - 1; i++) {
            for (int j = 0; j < model.getK() - 1; j++)  //y - i, x - j (а в лекциях соответствие обратное)
            {
                double f1 = model.getValue(j, i + 1);
                double f2 = model.getValue(j + 1, i + 1);
                double f3 = model.getValue(j, i);
                double f4 = model.getValue(j + 1, i);
                Point2D f1p = model.getPoint(j, i + 1);
                Point2D f2p = model.getPoint(j + 1, i + 1);
                Point2D f3p = model.getPoint(j, i);
                Point2D f4p = model.getPoint(j + 1, i);

                List<Point2D> points = new ArrayList<>();
                List<Seed> tempSeeds = new ArrayList<>();

                //double epsilon = (b-a)/10 + (d-c)/10;

                double epsilon = 1e-2;
                if (f1 == z)
                    f1 += epsilon;
                if (f2 == z)
                    f2 += epsilon;
                if (f3 == z)
                    f3 += epsilon;
                if (f4 == z)
                    f4 += epsilon;

                if (f1 < z && z < f2) {
                    Point2D p = new Point2D.Double(f1p.getX() + (f2p.getX() - f1p.getX()) * (z - f1) / (f2 - f1), f1p.getY());
                    points.add(p);
                    tempSeeds.add(new Seed(lesserColor, p.getX(), p.getY(), LEFT));
                    tempSeeds.add(new Seed(biggerColor, p.getX(), p.getY(), RIGHT));
                } else if (f1 > z && z > f2) {
                    Point2D p = new Point2D.Double(f1p.getX() + (f2p.getX() - f1p.getX()) * (1 - (z - f2) / (f1 - f2)), f1p.getY());
                    points.add(p);
                    tempSeeds.add(new Seed(lesserColor, p.getX(), p.getY(), RIGHT));
                    tempSeeds.add(new Seed(biggerColor, p.getX(), p.getY(), LEFT));
                }

                if (f3 < z && z < f4) {
                    Point2D p = new Point2D.Double(f3p.getX() + (f4p.getX() - f3p.getX()) * (z - f3) / (f4 - f3), f3p.getY());
                    points.add(p);
                    tempSeeds.add(new Seed(lesserColor, p.getX(), p.getY(), LEFT));
                    tempSeeds.add(new Seed(biggerColor, p.getX(), p.getY(), RIGHT));
                } else if (f3 > z && z > f4) {
                    Point2D p = new Point2D.Double(f3p.getX() + (f4p.getX() - f3p.getX()) * (1 - (z - f4) / (f3 - f4)), f3p.getY());
                    points.add(p);
                    tempSeeds.add(new Seed(lesserColor, p.getX(), p.getY(), RIGHT));
                    tempSeeds.add(new Seed(biggerColor, p.getX(), p.getY(), LEFT));
                }

                if (f1 > z && z > f3) {
                    Point2D p = new Point2D.Double(f1p.getX(), f3p.getY() + (f1p.getY() - f3p.getY()) * (z - f3) / (f1 - f3));
                    points.add(p);
                    tempSeeds.add(new Seed(lesserColor, p.getX(), p.getY(), DOWN));
                    tempSeeds.add(new Seed(biggerColor, p.getX(), p.getY(), UP));
                } else if (f1 < z && z < f3) {
                    Point2D p = new Point2D.Double(f1p.getX(), f3p.getY() + (f1p.getY() - f3p.getY()) * (1 - (z - f1) / (f3 - f1)));
                    points.add(p);
                    tempSeeds.add(new Seed(lesserColor, p.getX(), p.getY(), UP));
                    tempSeeds.add(new Seed(biggerColor, p.getX(), p.getY(), DOWN));
                }

                if (f2 > z && z > f4) {
                    Point2D p = new Point2D.Double(f2p.getX(), f4p.getY() + (f2p.getY() - f4p.getY()) * (z - f4) / (f2 - f4));
                    points.add(p);
                    tempSeeds.add(new Seed(lesserColor, p.getX(), p.getY(), DOWN));   //???
                    tempSeeds.add(new Seed(biggerColor, p.getX(), p.getY(), UP));
                } else if (f2 < z && z < f4) {
                    Point2D p = new Point2D.Double(f2p.getX(), f4p.getY() + (f2p.getY() - f4p.getY()) * (1 - (z - f2) / (f4 - f2)));
                    points.add(p);
                    tempSeeds.add(new Seed(lesserColor, p.getX(), p.getY(), UP));
                    tempSeeds.add(new Seed(biggerColor, p.getX(), p.getY(), DOWN));
                }

                if (points.size() == 2) {
                    Point2D p1 = points.get(0);
                    Point2D p2 = points.get(1);
                    //lines.add(p1);
                    //lines.add(p2);
                    lines.add(new Line(p1, p2, lesserColor));

                    if(seeds != null) {
                        seeds.addAll(tempSeeds);
                        Seed s1 = tempSeeds.get(0), s2 = tempSeeds.get(1), s3 = tempSeeds.get(2), s4 = tempSeeds.get(3);
                        seeds.add(new Seed(s1, s3));//их цвета одинаков
                        seeds.add(new Seed(s2, s4));//их цвета одинаков
                    }
                }
                else if (points.size() == 4)
                {
                    Point2D p1=null, p2=null, p3=null, p4=null;
                    Seed[] s = new Seed[8];
                    Seed s1=null, s2=null, s3=null, s4=null;

                    double f = (f1 + f2 + f3 + f4) / 4;
                    for (int k = 0; k < points.size(); k++)
                    {
                        Point2D p = points.get(k);
                        if(p.getY() == f3p.getY()) {
                            p1 = p;
                            s[0] = tempSeeds.get(k*2);
                            s[1] = tempSeeds.get(k*2 + 1);
                        }
                        else if (p.getX() == f2p.getX()) {
                            p2 = p;
                            s[2] = tempSeeds.get(k*2);
                            s[3] = tempSeeds.get(k*2 + 1);
                        }
                        else if (p.getX() == f3p.getX()) {
                            p3 = p;
                            s[4] = tempSeeds.get(k*2);
                            s[5] = tempSeeds.get(k*2 + 1);
                        }
                        else if (p.getY() == f1p.getY()) {
                            p4 = p;
                            s[6] = tempSeeds.get(k*2);
                            s[7] = tempSeeds.get(k*2 + 1);
                        }

                    }
                    if (f > z && z > f4 || f4 > z && z > f) {
                        //Линия проъодит через f3f4 и f4f2. а другая - соотв через f3f1 и f1f2 p1p2 & p3p4
                        lines.add(new Line(p1, p2, lesserColor));
                        lines.add(new Line(p1, p2, lesserColor));
                    }

                    else if (f > z && z > f3 || f3 > z && z > f)
                    {
                        //линия через f1f3 f3f4. другая p1p3 & p2p4
                        lines.add(new Line(p1, p3, lesserColor));
                        lines.add(new Line(p2, p4, lesserColor));
                    }
                    seeds.addAll(tempSeeds);

                }
            }
        }
    }


    public boolean isGridEnabled() {
        return isGridEnabled;
    }

    public void setGridEnabled(boolean gridEnabled) {
        isGridEnabled = gridEnabled;
        if(isGridEnabled) {
            drawGrid(mapPanel, mapModel);
            drawGrid(legendPanel.getLegendMap(), legendModel);
        }
        else {
            mapPanel.clearGrid();
            legendPanel.getLegendMap().clearGrid();
        }
        mapPanel.repaint();
        legendPanel.getLegendMap().repaint();

    }

    private void drawGrid(MapPanel mapPanel, Model model) {
        int width = mapPanel.getWidth(), height = mapPanel.getHeight();
        double a = model.getA(), b = model.getB(), c = model.getC(), d = model.getD();

        for(int i = 0; i < model.getM() - 1; i++) {
            for (int j = 0; j < model.getK() - 1; j++) {
                Point2D x2y2 = model.getPoint(j + 1, i + 1);
                Point2D x1y1 = model.getPoint(j, i);
                double x1 = x1y1.getX();
                double y1 = x1y1.getY();
                double x2 = x2y2.getX();
                double y2 = x2y2.getY();

                int u1 = (int)(width * (x1 - a)/(b - a) + 0.5);
                int u2 = (int)(width * (x2 - a)/(b - a) + 0.5);
                int v1 = (int)(height * (y1 - c)/(d - c) + 0.5);
                int v2 = (int)(height * (y2 - c)/(d - c) + 0.5);

                mapPanel.drawGridRect(u1, v1, u2, v2);


            }
        }
    }

    public double[] getRegionSizes()
    {
        return new double[]{mapModel.getA(), mapModel.getB(), mapModel.getC(), mapModel.getD()};
    }

    public boolean areIsolinesEnabled() {
        return areIsolinesEnabled;
    }

    public void setIsolinesEnabled(boolean isolinesEnabled) {
        this.areIsolinesEnabled = isolinesEnabled;
        mapPanel.setIsolinesEnabled(areIsolinesEnabled);
        mapPanel.repaint();
    }

    public int getK() {
        return mapModel.getK();
    }

    public int getM() {
        return mapModel.getM();
    }

    public void setModelConstants(int k, int m, double a, double b, double c, double d) {
        mapModel = new Model(k, m, a, b, c, d);

        mapPanel.clear();
        legendPanel.getLegendMap().clear();
        mapPanel.clearGrid();
        legendPanel.getLegendMap().clearGrid();
        mapPanel.setColor(isolineColor);
        legendPanel.getLegendMap().setColor(isolineColor);
        mapPanel.clearGridPoints();
        mapLines.clear();
        mapSeeds.clear();
        userLines.clear();
        dynamicUserLine.clear();
        calculateMap(mapModel, mapLines, mapSeeds);
        drawLegend();
        drawMap();

        mapPanel.repaint();
        legendPanel.repaint();
    }

    public boolean areGridPointsEnabled() {
        return areGridPointsEnabled;
    }

    public void setGridPointsEnabled(boolean gridPointsEnabled) {
        areGridPointsEnabled = gridPointsEnabled;
        mapPanel.setGridPointsEnabled(gridPointsEnabled);
        if(areGridPointsEnabled)
            recalculateAndDrawMap(mapPanel, mapModel, mapLines, mapSeeds);
        else
            mapPanel.clearGridPoints();
        mapPanel.repaint();
    }



    private int[] getRGB(int color)
    {
        return new int[] { (color & 0xFF0000) >> 16, (color & 0x00FF00) >> 8, color & 0x0000FF };
    }


    public void clearUserIsolines() {
        userLines.clear();
        dynamicUserLine.clear();
        mapPanel.clearUserLine();
        mapPanel.repaint();
    }

    public void setMode(int mode)
    {
        this.mode = mode;

        if(mode == SPAN)
        {
            mapPanel.setInterpolationEnabled(false);
            legendPanel.getLegendMap().setInterpolationEnabled(false);
            drawLegend();
            recalculateAndDrawMap(mapPanel, mapModel, mapLines, mapSeeds);
        }
        else if (mode == INTERPOLATION)
        {
            mapPanel.setInterpolationEnabled(true);
            legendPanel.getLegendMap().setInterpolationEnabled(true);
            drawLegend();
            recalculateAndDrawMap(mapPanel, mapModel, mapLines, mapSeeds);
        }
        else if (mode == PERPIXELACTUAL)
        {
            mapPanel.setInterpolationEnabled(false);
            legendPanel.getLegendMap().setInterpolationEnabled(false);
            mapPanel.clear();
            recalculateAndDrawMap(mapPanel, mapModel, mapLines, mapSeeds);
        }
        mapPanel.repaint();
        legendPanel.repaint();
    }
}
