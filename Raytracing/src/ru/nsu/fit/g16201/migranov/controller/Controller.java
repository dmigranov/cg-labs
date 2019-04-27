package ru.nsu.fit.g16201.migranov.controller;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Controller {
    private Color ambientLightColor;

    public int loadFile(File file) {

        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String[] substrings;

            substrings = readLineAndSplit(br);
            int ar = Integer.parseInt(substrings[0]);
            int ag = Integer.parseInt(substrings[1]);
            int ab = Integer.parseInt(substrings[2]);
            //todo: проверить границы
            ambientLightColor = new Color(ar, ag, ab);

            substrings = readLineAndSplit(br);
            int nl = Integer.parseInt(substrings[0]);       //число источников в сцене

            for (int i = 0; i < nl; i++)
            {

            }


        }
        catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e)
        {
            return -1;
        }

        return 0;
    }


    private String[] readLineAndSplit(BufferedReader br) throws IOException
    {
        String line;
        String[] substrings;
        do {
            line = br.readLine();
            line = line.substring(0, line.indexOf('/') != -1 ? line.indexOf('/') : line.length());
            substrings = line.split("\\s+");
        }
        while(substrings.length == 0 || "".equals(substrings[0]));
        return substrings;
    }
}
