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
    List<SimpleEntry<Point3D, Double>> charges = new ArrayList<>();
    List<SimpleEntry<Integer, Integer>> absorption = new ArrayList<>();
    List<SimpleEntry<Integer, Integer>> emission = new ArrayList<>();       //второе число - это ржб; перове - координата (0..100)



    void openConfigurationFile(File file)
    {

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;


        }
        catch(IOException e)
        {
            //todo: диалог
        }
    }
}
