package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Matrix;
import ru.nsu.fit.g16201.migranov.model.Point3D;

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
    private Matrix sceneRotateMatrix;


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

            /*Matrix sceneRotateMatrix = new Matrix(3, 3);
            for(int i = 0; i < 3; i++)
            {
                substrings = readLineAndSplit();
                sceneRotateMatrix.setRow(i, new double[] {Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2])});
            }*/
            sceneRotateMatrix = readMatrixByRow(3, 3);

            substrings = readLineAndSplit();
            backgroundColor = new Color(Integer.parseInt(substrings[0]), Integer.parseInt(substrings[1]), Integer.parseInt(substrings[2]));

            int figureCount;
            substrings = readLineAndSplit();
            figureCount = Integer.parseInt(substrings[0]);

            for (int i = 0; i < figureCount; i++)
            {
                substrings = readLineAndSplit();
                Color color = new Color(Integer.parseInt(substrings[0]), Integer.parseInt(substrings[1]), Integer.parseInt(substrings[2]));

                substrings = readLineAndSplit();
                Point3D center = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));

                Matrix rotateMatrix = readMatrixByRow(3, 3);

                substrings = readLineAndSplit();
                int splinePointCount = Integer.parseInt(substrings[0]);
                //цикл

            }

        }
        catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e)
        {
            return -1;
        }
        return 0;
    }

    private Matrix readMatrixByRow(int rows, int cols) throws IOException {
        String[] substrings;
        Matrix matrix = new Matrix(rows, cols);
        for(int i = 0; i < rows; i++)
        {
            substrings = readLineAndSplit();
            matrix.setRow(i, new double[] {Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2])});
        }
        return matrix;
    }

    private String[] readLineAndSplit() throws IOException
    {
        String line;
        line = br.readLine();
        line = line.substring(0, line.indexOf('/') != -1 ? line.indexOf('/') : line.length());
        return line.split("\\s+");
    }
}
