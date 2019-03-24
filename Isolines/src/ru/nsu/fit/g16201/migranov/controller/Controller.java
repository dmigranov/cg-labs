package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Model;
import ru.nsu.fit.g16201.migranov.view.LegendPanel;
import ru.nsu.fit.g16201.migranov.view.MapPanel;

import java.awt.*;
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
            legendModel = new Model(this.n + 2, 1, (x,y)->x, 0, legendPanel.getLegendWidth(), 0, legendPanel.getLegendHeight());    //todo: проверить! (+1?)

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
    }

    public void drawLegend() {
        //todo: сделать для случая с интерполяцией!

        //неинтерполировано:

        double min = mapModel.getMinValue();
        double max = mapModel.getMaxValue();
        /*double addition = (double)legendPanel.getLegendWidth()/(n+1), sum = addition;
        for(int i = 1; i <= n; i++)
        {
            legendPanel.drawVerticalLine((int)Math.round(sum));
            legendPanel.spanFill((int)Math.round(sum + 1), 1, legendColors.get(i).getRGB());
            sum+=addition;
        }
        legendPanel.spanFill(1, 1, legendColors.get(0).getRGB());
        legendPanel.repaint();*/

        for(int j = 0; j <= n; j++)
        {
            System.out.println(legendModel.getValue(j, 0));
            legendPanel.drawVerticalLine((int)Math.round(legendModel.getValue(j, 0)));
            legendPanel.spanFill(1 + (int)Math.round(legendModel.getValue(j, 0)), 1, legendColors.get(j).getRGB());
        }

    }
}
