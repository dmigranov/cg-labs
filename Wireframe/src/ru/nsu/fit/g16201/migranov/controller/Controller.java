package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Figure;
import ru.nsu.fit.g16201.migranov.model.Matrix;
import ru.nsu.fit.g16201.migranov.model.Point2D;
import ru.nsu.fit.g16201.migranov.model.Point3D;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

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

            sceneRotateMatrix = readMatrixByRow(3, 3);
            //todo: дополнить до 4x4?

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
                //todo: дополнить до 4x4?

                substrings = readLineAndSplit();
                int splinePointCount = Integer.parseInt(substrings[0]);
                List<Point2D> splinePoints = new ArrayList<>();
                for(int j = 0; j < splinePointCount; j++)
                {
                    substrings = readLineAndSplit();
                    Point2D splinePoint = new Point2D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]));
                    splinePoints.add(splinePoint);
                }
                Figure figure  = new Figure(center, color, rotateMatrix, splinePoints);

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
