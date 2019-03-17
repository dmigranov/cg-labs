package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.view.GraphicsPanel;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.AbstractMap.SimpleEntry;

//todo: он же гооврил, что ращмеры куба фиксирванные?
class VolumeRenderer {
    private List<SimpleEntry<Point3D, Double>> charges;
    private List<SimpleEntry<Integer, Double>> absorption; //целочисленная координата от 0 до 100 и значение абсорбции
    private List<SimpleEntry<Integer, Integer>> emission;       //второе число - это ржб; перове - координата (0..100)


    private BufferedReader br;

    int openConfigurationFile(File file)
    {
        try
        {
            charges  = new ArrayList<>();
            absorption  = new ArrayList<>();
            emission  = new ArrayList<>();

            br = new BufferedReader(new FileReader(file));

            String[] substrings;

            substrings = readLineAndSplit();
            int o = Integer.parseInt(substrings[0]);    //число вершин в графике абсорбции среды
            int cur = 0;    //это чтобы всё шло по порядку, иначе возникают странные графики
            while(o > 0)
            {
                substrings = readLineAndSplit();
                int m = Integer.parseInt(substrings[0]);
                double t = Double.parseDouble(substrings[1]);
                if (m < 0 || m > 100 || t < 0 || t > 1  || m < cur)
                    throw new Exception("Wrong absorption constants");
                cur = m;
                absorption.add(new SimpleEntry<>(m, t));
                o--;
            }
            //Collections.sort(absorption, new AbsorptionComparator());

            substrings = readLineAndSplit();
            int c = Integer.parseInt(substrings[0]);    //число вершин в каждом графике эмиссии среды
            cur = 0;
            while(c > 0)
            {
                substrings = readLineAndSplit();
                int n = Integer.parseInt(substrings[0]);
                int r = Integer.parseInt(substrings[1]);
                int g = Integer.parseInt(substrings[2]);
                int b = Integer.parseInt(substrings[3]);

                if (n < 0 || n > 100 || r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255 || n < cur)
                    throw new Exception("Wrong emission constants");
                cur = n;
                emission.add(new SimpleEntry<>(n, 0xFF000000 | (r << 16) | (g << 8) | b)); //так? или лучше Color? Или лучше тройку хранить?
                c--;
            }

            substrings = readLineAndSplit();
            int nq = Integer.parseInt(substrings[0]);    //число зарядов
            while(nq > 0)
            {
                substrings = readLineAndSplit();
                double x = Double.parseDouble(substrings[0]);
                double y = Double.parseDouble(substrings[1]);
                double z = Double.parseDouble(substrings[2]);
                double q = Double.parseDouble(substrings[3]);

                //if () //todo: какие условия?
                //    throw new Exception("Wrong emission constants");
                charges.add(new SimpleEntry<>(new Point3D(x,y,z), q)); //так? или лучше Color? Или лучше тройку хранить?
                nq--;
            }



        }
        catch(Exception e)
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

    void drawAbsorptionGraphic(GraphicsPanel absorptionPanel) {

        for(int i = 0; i < absorption.size() - 1; i++)
        {
            SimpleEntry<Integer, Double> p1 = absorption.get(i);
            SimpleEntry<Integer, Double> p2 = absorption.get(i + 1);

            int x1 = p1.getKey(), x2 = p2.getKey();
            double y1 = p1.getValue(), y2 = p2.getValue();

            absorptionPanel.drawLine((int)(x1*3.5),(int)((1 - y1)*200),(int)(x2*3.5),(int)((1 - y2)*200));
        }
    }

    void drawEmissionGraphic(GraphicsPanel emissionPanel) {
        for(int i = 0; i < emission.size() - 1; i++)
        {
            SimpleEntry<Integer, Integer> p1 = emission.get(i);
            SimpleEntry<Integer, Integer> p2 = emission.get(i + 1);

            int x1 = p1.getKey(), x2 = p2.getKey();
            int r1 = p1.getValue(), r2 = p2.getValue();

            for(int k = 0, a = -1; k < 24; k+=8, a++) {
                int color1 = r1 >> k & 0x000000FF;
                int color2 = r2 >> k & 0x000000FF;

                emissionPanel.drawLine((int)(x1*3.5) + a,(int)((1 - color1/255.0)*200) + a,(int)(x2*3.5) + a,(int)((1 - color2/255.0)*200) + a, 0x000000FF << k);

            }

        }
    }

    class AbsorptionComparator implements Comparator<SimpleEntry<Integer, Double>>
    {
        @Override
        public int compare(SimpleEntry<Integer, Double> a, SimpleEntry<Integer, Double> b)
        {
            return a.getKey() < b.getKey() ? -1 : a.getKey() == b.getKey() ? 0 : 1;
        }
    }
}


