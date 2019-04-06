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
        catch (IOException e)
        {
            return -1;
        }
        return 0; //todo убрать
    }
}
