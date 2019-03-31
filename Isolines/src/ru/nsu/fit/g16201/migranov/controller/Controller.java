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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private boolean areIsolinesEnabled;

    private List<Point2D> mapLines, legendLines;        //l1p1 l1p2 l2p1 l2p2
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
                if(x < 0 || x > mapPanel.getWidth() || y < 0 || y > mapPanel.getHeight())
                    return;
                statusLabel.setText(x + " " + y);

            }
        });
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                statusLabel.setText("");
            }
        });
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

            legendModel = new Model(this.n + 2, 2, (x,y)->x, 0, 1, 0, 1);    //отнормировал до 1

            mapPanel.updateSize();
            legendPanel.updateSize();
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
            calculateMap(mapModel, mapLines, mapSeeds);
            drawMap();
            calculateMap(legendModel, legendLines, legendSeeds);
            drawLegend();
            mapPanel.repaint();
            legendPanel.repaint();
        }
        catch (Exception e)
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
        //drawMap(mapPanel, mapModel);
        recalculateAndDrawMap(mapPanel, mapModel, mapLines, mapSeeds);
        if(isGridEnabled) {
            drawGrid(mapPanel, mapModel);
        }

    }

    private void drawLegend() {
        /*double min = mapModel.getMinValue();
        double max = mapModel.getMaxValue();
        for(int j = 0; j <= n; j++)
        {
            legendPanel.drawVerticalLine((int)Math.round(legendModel.getValue(j, 0)));
            legendPanel.spanFill(1 + (int)Math.round(legendModel.getValue(j, 0)), 1, legendColors.get(j).getRGB());
        }*/

        //drawMap(legendPanel.getLegendMap(), legendModel);
        recalculateAndDrawMap(legendPanel.getLegendMap(), legendModel, legendLines, legendSeeds);

        if(isGridEnabled) {
            drawGrid(legendPanel.getLegendMap(), legendModel);
        }
        legendPanel.drawText(n, mapModel.getMinValue(), mapModel.getMaxValue());

    }

    private void drawMap(MapPanel mapPanel, Model model)
    {
        //todo: сделать для случая с интерполяцией!

        for(int i = 0; i < model.getM() - 1; i++)
        {
            for (int j = 0; j < model.getK() - 1; j++) {     //y - i, x - j (а в лекциях соответствие обратное)
                double f1 = model.getValue(j, i + 1);
                double f2 = model.getValue(j + 1, i + 1);
                double f3 = model.getValue(j, i);
                double f4 = model.getValue(j + 1, i);
                Point2D f1p = model.getPoint(j, i + 1);
                Point2D f2p = model.getPoint(j + 1, i + 1);
                Point2D f3p = model.getPoint(j, i);
                Point2D f4p = model.getPoint(j + 1, i);

                for(int l = 1; l <= n; l++)     //так?
                {
                    List<Point2D> points = new ArrayList<>();

                    double z = model.getMinValue() + l * (model.getMaxValue() - model.getMinValue())/(n + 1);

                    //todo?
                    if(f1 == z)
                        f1 += epsilon;
                    if(f2 == z)
                        f2 += epsilon;
                    if(f3 == z)
                        f3 += epsilon;
                    if(f4 == z)
                        f4 += epsilon;

                    if(f1 < z && z < f2)
                    {
                        points.add(new Point2D.Double(f1p.getX() + (f2p.getX() - f1p.getX()) * (z - f1)/(f2 -f1), f1p.getY()));
                    }
                    else if(f1 > z && z > f2)
                    {
                        points.add(new Point2D.Double(f1p.getX() + (f2p.getX() - f1p.getX()) * (1 - (z - f2)/(f1 -f2)), f1p.getY()));
                    }
                    /*else if(f1 == z && f2 == z)
                    {
                        points.add(new Point2D.Double(f1p.getX(), f1p.getY()));
                        points.add(new Point2D.Double(f2p.getX(), f2p.getY()));
                    }*/ //прибавляю epsilon

                    if(f3 < z && z < f4)
                    {
                        points.add(new Point2D.Double(f3p.getX() + (f4p.getX() - f3p.getX()) * (z - f3)/(f4 -f3), f3p.getY()));
                    }
                    else if(f3 > z && z > f4)
                    {
                        points.add(new Point2D.Double(f3p.getX() + (f4p.getX() - f3p.getX()) * (1 - (z - f4)/(f3 -f4)), f3p.getY()));
                    }

                    if(f1 > z && z > f3)
                    {
                        points.add(new Point2D.Double(f1p.getX(), f3p.getY() + (f1p.getY() - f3p.getY()) * (z - f3)/(f1 -f3)));
                    }
                    else if(f1 < z && z < f3)
                    {
                        points.add(new Point2D.Double(f1p.getX(), f3p.getY() + (f1p.getY() - f3p.getY()) * (1 - (z - f1)/(f3 -f1))));
                    }

                    if(f2 > z && z > f4)
                    {
                        points.add(new Point2D.Double(f2p.getX(), f4p.getY() + (f2p.getY() - f4p.getY()) * (z - f4)/(f2 -f4)));
                    }
                    else if(f2 < z && z < f4)
                    {
                        points.add(new Point2D.Double(f2p.getX(), f4p.getY() + (f2p.getY() - f4p.getY()) * (1 - (z - f2)/(f4 -f2))));
                    }


                    if(points.size() == 2)
                    {
                        Point2D p1 = points.get(0);
                        Point2D p2 = points.get(1);
                        double x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2  = p2.getY();
                        int u1 = (int)(mapPanel.getWidth() * (x1 - model.getA())/(model.getB() - model.getA()) + 0.5);
                        int u2 = (int)(mapPanel.getWidth() * (x2 - model.getA())/(model.getB() - model.getA()) + 0.5);
                        int v1 = (int)(mapPanel.getHeight() * (y1 - model.getC())/(model.getD() - model.getC()) + 0.5);
                        int v2 = (int)(mapPanel.getHeight() * (y2 - model.getC())/(model.getD() - model.getC()) + 0.5);
                        mapPanel.drawLine(u1, v1, u2, v2);

                        Color lesserColor = legendColors.get(l - 1);
                        Color biggerColor = legendColors.get(l);
                        int us1=0, us2=0, vs1=0, vs2=0;
                        if(f1 > f2 || f1 < f2)
                        {
                            //todo
                            us1 = (int)(mapPanel.getWidth() * (f1p.getX() - model.getA())/(model.getB() - model.getA()) + 0.5);
                            us2 = (int)(mapPanel.getWidth() * (f2p.getX() - model.getA())/(model.getB() - model.getA()) + 0.5);
                            vs1 = (int)(mapPanel.getHeight() * (f1p.getY() - model.getC())/(model.getD() - model.getC()) + 0.5);
                            vs2 = (int)(mapPanel.getHeight() * (f2p.getY() - model.getC())/(model.getD() - model.getC()) + 0.5);

                            vs1 = vs1 <mapPanel.getHeight()?vs1:mapPanel.getHeight()-1;
                            vs2 = vs2 <mapPanel.getHeight()?vs2:mapPanel.getHeight()-1;
                            us1 = us1 < mapPanel.getWidth()? us1 : mapPanel.getWidth()-1;
                            us2 = us2 < mapPanel.getWidth()? us2 : mapPanel.getWidth()-1;

                            //todo: понимаю, в чём корень проблемы с легендой. во-первых, там сетка не совпадает с изолиниями полностью (из-за прибаления?(
                            // но проблема в том, что иногда совпадает! и если не совпадает, то там личния в два пикселя и закрашивания не проиходит
                            //try {
                                if (f1 > f2) {
                                    mapPanel.spanFill(us1, vs1, biggerColor.getRGB() );
                                    mapPanel.spanFill(us2, vs2, lesserColor.getRGB());
                                } else if (f2 > f1) {
                                    mapPanel.spanFill(us1, vs1,lesserColor.getRGB());
                                    mapPanel.spanFill(us2, vs2,biggerColor.getRGB());
                                }
                            /*}
                            catch(IndexOutOfBoundsException e)
                            {
                            }*/
                        }


                    }
                    else if(points.size() == 4)
                    {
                        double f = (f1+f2+f3+f4)/4;

                        //todo:
                    }


                }
            }
        }
    }

    //чтобы каждый раз не считать изолинии, сохранять их в лист и при ресайзе переводить из системы xy в uv
    private void recalculateAndDrawMap(MapPanel mapPanel, Model model, List<Point2D> lines, List<Seed> seeds)
    {
        int width = mapPanel.getWidth(), height = mapPanel.getHeight();
        double a = model.getA(), b = model.getB(), c = model.getC(), d = model.getD();
        for(int i = 0; i < lines.size(); i+=2)
        {
            Point2D p1 = lines.get(i);
            Point2D p2 = lines.get(i+1);

            double x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2  = p2.getY();
            int u1 = (int)(width * (x1 - a)/(b - a) + 0.5);
            int u2 = (int)(width * (x2 - a)/(b - a) + 0.5);
            int v1 = (int)(height * (y1 - c)/(d - c) + 0.5);
            int v2 = (int)(height * (y2 - c)/(d - c) + 0.5);

            mapPanel.drawLine(u1, v1, u2, v2);

        }

        for(Seed s : seeds)
        {
            double x = s.x, y = s.y;
            int color = s.color;

            int us = (int)(width * (x - a)/(b - a) + 0.5);
            int vs = (int)(height * (y - c)/(d - c) + 0.5);

            vs = vs < height ? vs : height - 1;
            us = us < width ? us : width - 1;

            mapPanel.spanFill(us, vs, color);


        }
    }

    //первый раз только при загрузке файла. в пространстве xy
    private void calculateMap(Model model, List<Point2D> lines, List<Seed> seeds)
    {
        for(int i = 0; i < model.getM() - 1; i++)
        {
            for (int j = 0; j < model.getK() - 1; j++) {     //y - i, x - j (а в лекциях соответствие обратное)
                double f1 = model.getValue(j, i + 1);
                double f2 = model.getValue(j + 1, i + 1);
                double f3 = model.getValue(j, i);
                double f4 = model.getValue(j + 1, i);
                Point2D f1p = model.getPoint(j, i + 1);
                Point2D f2p = model.getPoint(j + 1, i + 1);
                Point2D f3p = model.getPoint(j, i);
                Point2D f4p = model.getPoint(j + 1, i);

                for(int l = 1; l <= n; l++)     //так?
                {
                    List<Point2D> points = new ArrayList<>();

                    double z = model.getMinValue() + l * (model.getMaxValue() - model.getMinValue())/(n + 1);

                    //todo?
                    if(f1 == z)
                        f1 += epsilon;
                    if(f2 == z)
                        f2 += epsilon;
                    if(f3 == z)
                        f3 += epsilon;
                    if(f4 == z)
                        f4 += epsilon;

                    if(f1 < z && z < f2)
                    {
                        points.add(new Point2D.Double(f1p.getX() + (f2p.getX() - f1p.getX()) * (z - f1)/(f2 -f1), f1p.getY()));
                    }
                    else if(f1 > z && z > f2)
                    {
                        points.add(new Point2D.Double(f1p.getX() + (f2p.getX() - f1p.getX()) * (1 - (z - f2)/(f1 -f2)), f1p.getY()));
                    }

                    if(f3 < z && z < f4)
                    {
                        points.add(new Point2D.Double(f3p.getX() + (f4p.getX() - f3p.getX()) * (z - f3)/(f4 -f3), f3p.getY()));
                    }
                    else if(f3 > z && z > f4)
                    {
                        points.add(new Point2D.Double(f3p.getX() + (f4p.getX() - f3p.getX()) * (1 - (z - f4)/(f3 -f4)), f3p.getY()));
                    }

                    if(f1 > z && z > f3)
                    {
                        points.add(new Point2D.Double(f1p.getX(), f3p.getY() + (f1p.getY() - f3p.getY()) * (z - f3)/(f1 -f3)));
                    }
                    else if(f1 < z && z < f3)
                    {
                        points.add(new Point2D.Double(f1p.getX(), f3p.getY() + (f1p.getY() - f3p.getY()) * (1 - (z - f1)/(f3 -f1))));
                    }

                    if(f2 > z && z > f4)
                    {
                        points.add(new Point2D.Double(f2p.getX(), f4p.getY() + (f2p.getY() - f4p.getY()) * (z - f4)/(f2 -f4)));
                    }
                    else if(f2 < z && z < f4)
                    {
                        points.add(new Point2D.Double(f2p.getX(), f4p.getY() + (f2p.getY() - f4p.getY()) * (1 - (z - f2)/(f4 -f2))));
                    }


                    if(points.size() == 2)
                    {
                        Point2D p1 = points.get(0);
                        Point2D p2 = points.get(1);
                        lines.add(p1);
                        lines.add(p2);

                        Color lesserColor = legendColors.get(l - 1);
                        Color biggerColor = legendColors.get(l);
                        if(f1 > z && z > f2 || f1 < z && z < f2)
                        {
                            //todo
                            if (f2 < f1) {
                                seeds.add(new Seed(biggerColor, f1p.getX(), f1p.getY()));
                                seeds.add(new Seed(lesserColor, f2p.getX(), f2p.getY()));
                            } else if (f2 > f1) {
                                seeds.add(new Seed(lesserColor, f1p.getX(), f1p.getY()));
                                seeds.add(new Seed(biggerColor, f2p.getX(), f2p.getY()));
                            }
                        }
                        if(f3 > z && z > f4 || f3 < z && z < f4)
                        {
                            if (f4 < f3) {
                                seeds.add(new Seed(biggerColor, f3p.getX(), f3p.getY()));
                                seeds.add(new Seed(lesserColor, f4p.getX(), f4p.getY()));
                            } else if (f4 > f3) {
                                seeds.add(new Seed(lesserColor, f3p.getX(), f3p.getY()));
                                seeds.add(new Seed(biggerColor, f4p.getX(), f4p.getY()));
                            }
                        }
                    }
                    else if(points.size() == 4)
                    {
                        double f = (f1+f2+f3+f4)/4;

                        //todo:
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
    }

    public int getK() {
        return mapModel.getK();
    }

    public int getM() {
        return mapModel.getM();
    }

    public void setModelConstants(int k, int m, double a, double b, double c, double d) {
        mapModel = new Model(k, m, a, b, c, d);
        //todo: legendModel

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
        calculateMap(mapModel, mapLines, mapSeeds);
        drawMap();
        calculateMap(legendModel, legendLines, legendSeeds);
        drawLegend();
        mapPanel.repaint();
        legendPanel.repaint();
    }
}
