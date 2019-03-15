package ru.nsu.fit.g16201.migranov.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.AbstractMap.SimpleEntry;

//todo: он же гооврил, что ращмеры куба фиксирванные?
class VolumeRenderer {
    private List<SimpleEntry<Point3D, Double>> charges = new ArrayList<>();
    private List<SimpleEntry<Integer, Double>> absorption = new ArrayList<>(); //целочисленная координата от 0 до 100 и значение абсорбции
    private List<SimpleEntry<Integer, Integer>> emission = new ArrayList<>();       //второе число - это ржб; перове - координата (0..100)


    private BufferedReader br;

    public void openConfigurationFile(File file)
    {

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            this.br = br;

            String[] substrings;

            substrings = readLineAndSplit();
            int o = Integer.parseInt(substrings[0]);    //число вершин в графике абсорбции среды
            while(o > 0)
            {
                substrings = readLineAndSplit();
                int m = Integer.parseInt(substrings[0]);
                double t = Double.parseDouble(substrings[1]);
                if (m < 0 || m > 100 || t < 0 || t > 1)
                    throw new Exception("Wrong absorption constants");
                absorption.add(new SimpleEntry<>(m, t));
                o--;
            }

            substrings = readLineAndSplit();
            int с = Integer.parseInt(substrings[0]);    //число вершин в каждом графике эмиссии среды
            while(с > 0)
            {
                substrings = readLineAndSplit();
                int n = Integer.parseInt(substrings[0]);
                int r = Integer.parseInt(substrings[1]);
                int g = Integer.parseInt(substrings[2]);
                int b = Integer.parseInt(substrings[3]);

                if (n < 0 || n > 100 || r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
                    throw new Exception("Wrong emission constants");
                emission.add(new SimpleEntry<>(n, 0xFF000000 | (r << 16) | (g << 8) | b)); //так? или лучше Color? Или лучше тройку хранить?
                с--;
            }




        }
        catch(Exception e)
        {
            //todo: диалог
            System.out.println("who is general fault");
        }
    }

    private String[] readLineAndSplit() throws IOException
    {
        String line;
        line = br.readLine();
        line = line.substring(0, line.indexOf('/') != -1 ? line.indexOf('/') : line.length());
        return line.split("\\s+");
    }
}
