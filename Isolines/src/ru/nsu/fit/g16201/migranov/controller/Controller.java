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
    private final MapPanel mapPanel;
    private final LegendPanel legendPanel;
    private BufferedReader br;
    private List<Color> legendColors = new ArrayList<>();
    private Color isolineColor;
    private Model mapModel, legendModel;
    private int n;      //количество цветов (на самом деле уменьшенное на единицу, т.к. c0, c1, ..., cn)

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
            legendModel = new Model(this.n + 2, 1, (x,y)->x, 0, 1, 0, 1);    //отнормировал


            substrings = readLineAndSplit();
            int r = Integer.parseInt(substrings[0]);
            int g = Integer.parseInt(substrings[1]);
            int b = Integer.parseInt(substrings[2]);
            isolineColor = new Color(r, g, b);


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
    }

    public void drawLegend() {
        //todo: сделать для случая с интерполяцией!

        //неинтерполировано:
        double min = mapModel.getMinValue();
        double max = mapModel.getMaxValue();

        for(int j = 0; j <= n; j++)
        {
            legendPanel.drawVerticalLine((int)Math.round(legendModel.getValue(j, 0)));
            legendPanel.spanFill(1 + (int)Math.round(legendModel.getValue(j, 0)), 1, legendColors.get(j).getRGB());
        }

        //drawMap(legendPanel.getLegendMap(), legendModel);

    }

    public void drawMap(MapPanel mapPanel, Model model)
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


                for(int l = 0; l <= n; l++) //по всем цветовым уровням
                {
                    List<Point2D> points = new ArrayList<>();
                    double z = legendModel.getValue(l, 0) * (model.getMaxValue() - model.getMinValue()) + model.getMinValue();

                    if(f1 < z && z < f2)
                    {
                        points.add(new Point2D.Double(f1p.getX() + (f2p.getX() - f1p.getX()) * (z - f1)/(f2 -f1), f1p.getY()));
                    }
                    else if(f1 > z && z > f2)
                    {
                        //todo: проверить
                        points.add(new Point2D.Double(f1p.getX() + (f2p.getX() - f1p.getX()) * (1 - (z - f2)/(f1 -f2)), f1p.getY()));
                    }

                    if(f3 < z && z < f4)
                    {
                        points.add(new Point2D.Double(f3p.getX() + (f4p.getX() - f3p.getX()) * (z - f3)/(f4 -f3), f3p.getY()));
                    }
                    else if(f1 > z && z > f2)
                    {
                        points.add(new Point2D.Double(f3p.getX() + (f4p.getX() - f3p.getX()) * (1 - (z - f4)/(f3 -f4)), f3p.getY()));
                    }


                    if(points.size() == 2)
                    {
                        Point2D p1 = points.get(0);
                        Point2D p2 = points.get(1);
                        double x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2  = p2.getY();
                        System.out.println(x1 + " " +  y1 + " " + x2 + " " + y2);
                        int u1 = (int)(mapPanel.getWidth() * (x1 - model.getA())/(model.getB() - model.getA()) + 0.5);
                        int u2 = (int)(mapPanel.getWidth() * (x2 - model.getA())/(model.getB() - model.getA()) + 0.5);

                        int v1 = (int)(mapPanel.getHeight() * (y1 - model.getC())/(model.getD() - model.getC()) + 0.5);
                        int v2 = (int)(mapPanel.getHeight() * (y2 - model.getC())/(model.getD() - model.getC()) + 0.5);
                       //ystem.out.println(u1 + " " +  v1 + " " + u2 + " " + v2);
                        mapPanel.drawLine(u1, v1, u2, v2);
                    }

                }
            }
        }
    }


}
