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

import static ru.nsu.fit.g16201.migranov.controller.Seed.LEFT;
import static ru.nsu.fit.g16201.migranov.controller.Seed.RIGHT;

public class Controller {
    private static double epsilon = 1e-3;
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
    private boolean interpolationEnabled = false;
    private boolean perPixelColorMapEnabled = false;

    private List<Point2D> mapLines, legendLines, userLines;        //l1p1 l1p2 l2p1 l2p2
    //private Set<Seed> mapSeeds, legendSeeds;
    private List<Seed> mapSeeds, legendSeeds;


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
                    drawMap();
                    recalculateAndDrawUserLines();
                    drawLegend();
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
        });
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                mapPanel.clearUserLine();
                int x = e.getX(), y = e.getY();
                if (x < 0 || x > mapPanel.getWidth() || y < 0 || y > mapPanel.getHeight() || mapModel == null)
                    return;

                double mx = ((mapModel.getB() - mapModel.getA()) * x / mapPanel.getWidth() + mapModel.getA());
                double my = ((mapModel.getD() - mapModel.getC()) * y / mapPanel.getHeight() + mapModel.getC());
                double z = mapModel.applyFunction(mx, my);

                //calculateDynamicIsoline(z);
                calculateMapForLevel(mapModel, userLines, null, z, 0);
                recalculateAndDrawUserLines();

                mapPanel.repaint();
            }
        });
    }

    private void recalculateAndDrawUserLines() {
        int width = mapPanel.getWidth(), height = mapPanel.getHeight();
        double a = mapModel.getA(), b = mapModel.getB(), c = mapModel.getC(), d = mapModel.getD();
        for(int i = 0; i < userLines.size(); i+=2)
        {
            Point2D p1 = userLines.get(i);
            Point2D p2 = userLines.get(i+1);

            double x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2  = p2.getY();
            int u1 = (int)(width * (x1 - a)/(b - a) + 0.5);
            int u2 = (int)(width * (x2 - a)/(b - a) + 0.5);
            int v1 = (int)(height * (y1 - c)/(d - c) + 0.5);
            int v2 = (int)(height * (y2 - c)/(d - c) + 0.5);

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
            mapSeeds = new ArrayList<>();
            legendLines = new ArrayList<>();
            legendSeeds = new ArrayList<>();
            userLines = new ArrayList<>();
            calculateMap(mapModel, mapLines, mapSeeds);
            drawMap();
            calculateMap(legendModel, legendLines, legendSeeds);
            drawLegend();
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

        if(!interpolationEnabled)
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
                if (u < colors[0]) {
                    legendMap.drawLineInterpolated(u, 0, u, legendMap.getHeight(), legendColors.get(0).getRGB());
                    continue;
                }
                if (u > colors[n]) {
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
        legendPanel.drawText(n, mapModel.getMinValue(), mapModel.getMaxValue());

    }


    //чтобы каждый раз не считать изолинии, сохранять их в лист и при ресайзе переводить из системы xy в uv
    private void recalculateAndDrawMap(MapPanel mapPanel, Model model, List<Point2D> lines, List<Seed> seeds)
    {
        int width = mapPanel.getWidth(), height = mapPanel.getHeight();
        double a = model.getA(), b = model.getB(), c = model.getC(), d = model.getD();

        for (int i = 0; i < lines.size(); i += 2) {
            Point2D p1 = lines.get(i);
            Point2D p2 = lines.get(i + 1);

            double x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2 = p2.getY();
            int u1 = (int) (width * (x1 - a) / (b - a) + 0.5);
            int u2 = (int) (width * (x2 - a) / (b - a) + 0.5);
            int v1 = (int) (height * (y1 - c) / (d - c) + 0.5);
            int v2 = (int) (height * (y2 - c) / (d - c) + 0.5);

            mapPanel.drawLine(u1, v1, u2, v2);
            if (areGridPointsEnabled) {
                mapPanel.drawGridPoint(u1, v1);
                mapPanel.drawGridPoint(u2, v2);
            }
        }
        if(perPixelColorMapEnabled) {
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
        else {

            for (Seed s : seeds) {
                double x = s.x, y = s.y;
                int color = s.color;

                int us = (int) (width * (x - a) / (b - a) + 0.5);
                int vs = (int) (height * (y - c) / (d - c) + 0.5);

                vs = vs < height ? vs : height - 1;
                us = us < width ? us : width - 1;

                try {
                    mapPanel.spanFill(us, vs, color);
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }

        }

        if(interpolationEnabled)
        {
            //todo исправить
            double wk = (double)mapPanel.getWidth() / (model.getK() - 1); //размер ячейки
            double vm = (double)mapPanel.getHeight() / (model.getM() - 1);
            for(int v = 0; v < mapPanel.getHeight(); v++) {
                for (int u = 0; u < mapPanel.getWidth(); u++) {
                    int j =  (int)(u / wk);
                    int i =  (int)(v / vm);

                    int u0 = (int)wk*j, v0 = (int)vm*i;
                    if(i == 0)
                        v0++;
                    if(j==0)
                        u0++;
                    int u1 = (int)wk*(j+1), v1 = (int)vm*(i+1);
                    u1 = u1 < mapPanel.getWidth() ? u1 : mapPanel.getWidth() - 1;
                    v1 = v1 < mapPanel.getHeight() ? v1 : mapPanel.getHeight() - 1;

                    //System.out.println(u0 + " " + u + " " + u1 + "; " + v0 + " " + v + " " + v1);

                    int c00 = mapPanel.getRGB(u0, v0);
                    int c10 = mapPanel.getRGB(u1, v0);
                    int c01 = mapPanel.getRGB(u0, v1);
                    int c11 = mapPanel.getRGB(u1, v1);

                    int newColor = 0;

                    for(int k = 0; k < 24; k+=8) {
                        int cc00 = c00 >> k & 0x000000FF;
                        int cc10 = c10 >> k & 0x000000FF;
                        int cc01 = c01 >> k & 0x000000FF;
                        int cc11 = c11 >> k & 0x000000FF;

                        int ccx0 = cc00 * (u1 - u)/(u1 - u0) + cc10 * (u-u0)/(u1-u0);       //по верхнему ребру
                        int ccx1 = cc01 * (u1 - u)/(u1 - u0) + cc11 * (u-u0)/(u1-u0);       //по нижнему ребру

                        int ccxx = ccx0 * (v1 - v)/(v1 - v0) + ccx1 * (v - v0)/(v1 - v0);
                        if(ccxx < 0)
                            ccxx = 0;
                        if(ccxx > 255)
                            ccxx = 255;
                        newColor |= (ccxx << k);
                    }
                    mapPanel.paintPixelInterpolated(u, v, newColor);
                }
            }
            System.out.println();
        }
    }

    //первый раз только при загрузке файла. в пространстве xy
    private void calculateMap(Model model, List<Point2D> lines, List<Seed> seeds)
    {
        for(int l = 1; l <= n; l++)     //так?
        {
            double z = model.getMinValue() + l * (model.getMaxValue() - model.getMinValue()) / (n + 1);
            calculateMapForLevel(model, lines, seeds, z, l);
        }
    }

    private void calculateMapForLevel(Model model, List<Point2D> lines, List<Seed> seeds, double z, int l) {

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
                    tempSeeds.add(new Seed(biggerColor, p.getX(), p.getY(), LEFT));
                    tempSeeds.add(new Seed(lesserColor, p.getX(), p.getY(), RIGHT));
                }

                if (f3 < z && z < f4) {
                    Point2D p = new Point2D.Double(f3p.getX() + (f4p.getX() - f3p.getX()) * (z - f3) / (f4 - f3), f3p.getY());
                    points.add(p);
                    tempSeeds.add(new Seed(lesserColor, p.getX() - epsilonx, p.getY()));
                    tempSeeds.add(new Seed(biggerColor, p.getX() + epsilonx, p.getY()));
                } else if (f3 > z && z > f4) {
                    Point2D p = new Point2D.Double(f3p.getX() + (f4p.getX() - f3p.getX()) * (1 - (z - f4) / (f3 - f4)), f3p.getY());
                    points.add(p);
                    tempSeeds.add(new Seed(biggerColor, p.getX() - epsilonx, p.getY()));
                    tempSeeds.add(new Seed(lesserColor, p.getX() + epsilonx, p.getY()));
                }

                if (f1 > z && z > f3) {
                    Point2D p = new Point2D.Double(f1p.getX(), f3p.getY() + (f1p.getY() - f3p.getY()) * (z - f3) / (f1 - f3));
                    points.add(p);
                    tempSeeds.add(new Seed(lesserColor, p.getX(), p.getY() - epsilony));   //???
                    tempSeeds.add(new Seed(biggerColor, p.getX(), p.getY() + epsilony));
                } else if (f1 < z && z < f3) {
                    Point2D p = new Point2D.Double(f1p.getX(), f3p.getY() + (f1p.getY() - f3p.getY()) * (1 - (z - f1) / (f3 - f1)));
                    points.add(p);
                    tempSeeds.add(new Seed(biggerColor, p.getX(), p.getY() - epsilony));
                    tempSeeds.add(new Seed(lesserColor, p.getX(), p.getY() + epsilony));
                }

                if (f2 > z && z > f4) {
                    Point2D p = new Point2D.Double(f2p.getX(), f4p.getY() + (f2p.getY() - f4p.getY()) * (z - f4) / (f2 - f4));
                    points.add(p);
                    tempSeeds.add(new Seed(lesserColor, p.getX(), p.getY() - epsilony));
                    tempSeeds.add(new Seed(biggerColor, p.getX(), p.getY() + epsilony));
                } else if (f2 < z && z < f4) {
                    Point2D p = new Point2D.Double(f2p.getX(), f4p.getY() + (f2p.getY() - f4p.getY()) * (1 - (z - f2) / (f4 - f2)));
                    points.add(p);
                    tempSeeds.add(new Seed(biggerColor, p.getX(), p.getY() - epsilony));
                    tempSeeds.add(new Seed(lesserColor, p.getX(), p.getY() + epsilony));
                }

                if (points.size() == 2) {
                    Point2D p1 = points.get(0);
                    Point2D p2 = points.get(1);
                    lines.add(p1);
                    lines.add(p2);


                    if(seeds != null) {
                        seeds.addAll(tempSeeds);

                        if (f1 > z && z > f2 || f1 < z && z < f2) {
                           if (f2 < f1) {
                                seeds.add(new Seed(biggerColor, f1p.getX(), f1p.getY()));
                                seeds.add(new Seed(lesserColor, f2p.getX(), f2p.getY()));
                            } else if (f2 > f1) {
                                seeds.add(new Seed(lesserColor, f1p.getX(), f1p.getY()));
                                seeds.add(new Seed(biggerColor, f2p.getX(), f2p.getY()));
                            }
                        }
                        if (f3 > z && z > f4 || f3 < z && z < f4) {
                            if (f4 < f3) {
                                seeds.add(new Seed(biggerColor, f3p.getX(), f3p.getY()));
                                seeds.add(new Seed(lesserColor, f4p.getX(), f4p.getY()));
                            } else if (f4 > f3) {
                                seeds.add(new Seed(lesserColor, f3p.getX(), f3p.getY()));
                                seeds.add(new Seed(biggerColor, f4p.getX(), f4p.getY()));
                            }
                        }
                        /*if (f1 > z && z > f3 || f1 < z && z < f3) {
                            if (f1 > f3) {
                                seeds.add(new Seed(biggerColor, f1p.getX(), f1p.getY()));
                                seeds.add(new Seed(lesserColor, f3p.getX(), f3p.getY()));
                            } else if (f1 < f3) {
                                seeds.add(new Seed(lesserColor, f1p.getX(), f1p.getY()));
                                seeds.add(new Seed(biggerColor, f3p.getX(), f3p.getY()));
                            }
                        }*/

                    }
                }
                else if (points.size() == 4)
                {
                    double f = (f1 + f2 + f3 + f4) / 4;

                    //todo: создать список точек добавить туда пересечение
                    if (f > z && z > f4 || f4 > z && z > f) {
                        //Линия проъодит через f3f4 и f4f2. а другая - соотв через f3f1 и f1f2
                    }

                    else if (f > z && z > f3 || f3 > z && z > f)
                    {
                        //линия через f1f3 f3f4. другая
                    }

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
        //todo
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
        mapLines = new ArrayList<>();
        mapSeeds = new ArrayList<>();
        legendLines = new ArrayList<>();
        legendSeeds = new ArrayList<>();
        userLines = new ArrayList<>();
        calculateMap(mapModel, mapLines, mapSeeds);
        drawMap();
        calculateMap(legendModel, legendLines, legendSeeds);
        drawLegend();
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
        //todo: Точки входа в треугольники (возможно, занести в отдельынй массив и рисовать их там же (в отдельном цикле)
    }

    public boolean isInterpolationEnabled() {
        return interpolationEnabled;
    }

    public void setInterpolationEnabled(boolean interpolationEnabled) {
        this.interpolationEnabled = interpolationEnabled;
        mapPanel.setInterpolationEnabled(interpolationEnabled);
        legendPanel.getLegendMap().setInterpolationEnabled(interpolationEnabled);
        recalculateAndDrawMap(mapPanel, mapModel, mapLines, mapSeeds);
        drawLegend();

        mapPanel.repaint();
        legendPanel.repaint();
    }



    private int[] getRGB(int color)
    {
        return new int[] { (color & 0xFF0000) >> 16, (color & 0x00FF00) >> 8, color & 0x0000FF };
    }

    public boolean isPerPixelColorMapEnabled() {
        return perPixelColorMapEnabled;
    }

    public void setPerPixelColorMapEnabled(boolean perPixelColorMapEnabled) {
        this.perPixelColorMapEnabled = perPixelColorMapEnabled;

        mapPanel.clear();
        recalculateAndDrawMap(mapPanel, mapModel, mapLines, mapSeeds);
        mapPanel.repaint();

    }
}
