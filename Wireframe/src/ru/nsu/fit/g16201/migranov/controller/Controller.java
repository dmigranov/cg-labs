package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Matrix;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Controller {
    private int n, m, k;
    private double a, b, c, d;
    private double zn, zf, sw, sh;  //расстояние до ближней/дальней клиппирующей плоскости; размеры грани объёма визуализации на ближней плоскости
    private Color backgroundColor;


    private BufferedReader br;

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

            Matrix sceneRotateMatrix = new Matrix(3, 3);
            for(int i = 0; i < 3; i++)
            {
                substrings = readLineAndSplit();
                sceneRotateMatrix.setRow(i, new double[] {Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2])});
            }

            substrings = readLineAndSplit();
            backgroundColor = new Color(Integer.parseInt(substrings[0]), Integer.parseInt(substrings[1]), Integer.parseInt(substrings[2]));

            int figureCount;
            substrings = readLineAndSplit();
            figureCount = Integer.parseInt(substrings[0]);

            for (int i = 0; i < figureCount; i++)
            {
                substrings = readLineAndSplit();substrings = readLineAndSplit();
                int r = Integer.parseInt(substrings[0]), g = Integer.parseInt(substrings[1]), b = Integer.parseInt(substrings[2]);

                substrings = readLineAndSplit();substrings = readLineAndSplit();
                double cx = Double.parseDouble(substrings[0]), cy = Double.parseDouble(substrings[1]), cz = Double.parseDouble(substrings[2]);


            }

        }
        catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e)
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
}
