package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Model;
import ru.nsu.fit.g16201.migranov.view.LegendPanel;
import ru.nsu.fit.g16201.migranov.view.MapPanel;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private static double epsilon = 1e-3;
    private final MapPanel mapPanel;
    private final LegendPanel legendPanel;
    private BufferedReader br;
    private List<Color> legendColors = new ArrayList<>();
    private Color isolineColor;
    private Model mapModel, legendModel;
    private int n;      //количество цветов (на самом деле уменьшенное на единицу, т.к. c0, c1, ..., cn)

    private boolean isGridEnabled = false;
    private boolean areIsolinesEnabled;

    public Controller(MapPanel mapPanel, LegendPanel legendPanel) {
        this.mapPanel = mapPanel;
        this.legendPanel = legendPanel;
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
            //"линейная функция"
            //legendModel = new Model(this.n + 2, 1, (x,y)->x, 0, legendPanel.getLegendWidth(), 0, legendPanel.getLegendHeight());    //todo: проверить! (+1?)
            legendModel = new Model(this.n + 2, 2, (x,y)->x, 0, 1, 0, 1);    //отнормировал


            substrings = readLineAndSplit();
            int r = Integer.parseInt(substrings[0]);
            int g = Integer.parseInt(substrings[1]);
            int b = Integer.parseInt(substrings[2]);
            isolineColor = new Color(r, g, b);
            mapPanel.clear();
            legendPanel.getLegendMap().clear();
            mapPanel.clearGrid();
            legendPanel.getLegendMap().clearGrid();
            mapPanel.setColor(isolineColor);

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

    public void drawMap() {
        drawMap(mapPanel, mapModel);
        if(isGridEnabled) {
            drawGrid(mapPanel, mapModel);
        }
    }

    public void drawLegend() {
        //todo: сделать для случая с интерполяцией!

        //неинтерполировано:
        /*double min = mapModel.getMinValue();
        double max = mapModel.getMaxValue();

        for(int j = 0; j <= n; j++)
        {
            legendPanel.drawVerticalLine((int)Math.round(legendModel.getValue(j, 0)));
            legendPanel.spanFill(1 + (int)Math.round(legendModel.getValue(j, 0)), 1, legendColors.get(j).getRGB());
        }*/

        drawMap(legendPanel.getLegendMap(), legendModel);
        if(isGridEnabled) {
            drawGrid(legendPanel.getLegendMap(), legendModel);
        }
        legendPanel.drawText(n, mapModel.getMinValue(), mapModel.getMaxValue());

    }

    private void drawMap(MapPanel mapPanel, Model model)
    {
        for(int i = 0; i < model.getM() - 1; i++)
        {      //y - i
            for (int j = 0; j < model.getK() - 1; j++) {     //x - j (а в лекциях соответствие обратное)
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
                    Point2D lesserColorSeed;

                    //double z = legendModel.getValue(l, 0) * (model.getMaxValue() - model.getMinValue()) + model.getMinValue();
                    double z = model.getMinValue() + l * (model.getMaxValue() - model.getMinValue())/(n + 1);

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
                        //System.out.println(x1 + " " +  y1 + " " + x2 + " " + y2);
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
                            vs2 = vs2 <mapPanel.getHeight()?vs1:mapPanel.getHeight()-1;
                            us1 = us1 < mapPanel.getWidth()? us1 : mapPanel.getWidth()-1;
                            us2 = us2 < mapPanel.getWidth()? us2 : mapPanel.getWidth()-1;

                            if(f1 > f2) {
                                mapPanel.spanFill(us1, vs1, lesserColor.getRGB());
                                mapPanel.spanFill(us2, vs2, biggerColor.getRGB());
                            }
                            else if (f2 > f1)
                            {
                                mapPanel.spanFill(us1, vs1, biggerColor.getRGB());
                                mapPanel.spanFill(us2, vs2, lesserColor.getRGB());
                            }
                        }




                        /*try {
                            mapPanel.spanFill(us1, vs1, lesserColor.getRGB());
                            mapPanel.spanFill(us2, vs2, biggerColor.getRGB());    //в будущем я расмотрю для все хслучаев
                        }
                        catch(IndexOutOfBoundsException e)
                        {
                            int o = 5;
                        }*/


                    }
                    else if(points.size() == 4)
                    {
                        double f = (f1+f2+f3+f4)/4;
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
        for(int i = 0; i < model.getM() - 1; i++) {
            for (int j = 0; j < model.getK() - 1; j++) {
                //Point2D x1y2 = model.getPoint(j, i + 1);
                Point2D x2y2 = model.getPoint(j + 1, i + 1);
                Point2D x1y1 = model.getPoint(j, i);
                //Point2D x2y1 = model.getPoint(j + 1, i);
                double x1 = x1y1.getX();
                double y1 = x1y1.getY();
                double x2 = x2y2.getX();
                double y2 = x2y2.getY();

                int u1 = (int)(mapPanel.getWidth() * (x1 - model.getA())/(model.getB() - model.getA()) + 0.5);
                int u2 = (int)(mapPanel.getWidth() * (x2 - model.getA())/(model.getB() - model.getA()) + 0.5);
                int v1 = (int)(mapPanel.getHeight() * (y1 - model.getC())/(model.getD() - model.getC()) + 0.5);
                int v2 = (int)(mapPanel.getHeight() * (y2 - model.getC())/(model.getD() - model.getC()) + 0.5);

                mapPanel.drawGridLine(u1, v1, u1, v2);
                mapPanel.drawGridLine(u1, v1, u2, v1);
                mapPanel.drawGridLine(u2, v2, u2, v1);
                mapPanel.drawGridLine(u2, v2, u1, v2);



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

}
