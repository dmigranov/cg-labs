package ru.nsu.fit.g16201.migranov.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Controller {
    private int n, m, k;
    private double a, b, c, d;
    private double zn, zf, sw, sh;  //расстояние до ближней/дальней клиппирующей плоскости; размеры грани объёма визуализации на ближней плоскости

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


        }
        catch (IOException e)
        {
            return -1;
        }
        return 0; //todo убрать
    }

    private String[] readLineAndSplit() throws IOException
    {
        String line;
        line = br.readLine();
        line = line.substring(0, line.indexOf('/') != -1 ? line.indexOf('/') : line.length());
        return line.split("\\s+");
    }
}
