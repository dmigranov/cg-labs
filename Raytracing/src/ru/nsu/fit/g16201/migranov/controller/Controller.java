package ru.nsu.fit.g16201.migranov.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Controller {
    public int loadFile(File file) {

        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {

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
        line = br.readLine();
        line = line.substring(0, line.indexOf('/') != -1 ? line.indexOf('/') : line.length());
        return line.split("\\s+");
    }
}
