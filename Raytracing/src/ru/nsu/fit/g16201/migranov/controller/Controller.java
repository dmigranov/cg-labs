package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Light;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.model.primitives.Box;
import ru.nsu.fit.g16201.migranov.model.primitives.Primitive;
import ru.nsu.fit.g16201.migranov.model.primitives.Sphere;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private Color ambientLightColor;

    private List<Light> lights;
    private List<Primitive> primitives;     //использовать только для вайрфрейма? а то оптимизация...

    private boolean isRenderFileLoaded = false;

    public int loadFile(File file) {
        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String[] substrings;

            substrings = readLineAndSplit(br);
            int ar = Integer.parseInt(substrings[0]);
            int ag = Integer.parseInt(substrings[1]);
            int ab = Integer.parseInt(substrings[2]);

            if(ar < 0 || ar > 255 || ag < 0 || ag > 255 || ab < 0 || ab > 255)
                throw new IOException("Wrong ambient color");

            ambientLightColor = new Color(ar, ag, ab);

            substrings = readLineAndSplit(br);
            int nl = Integer.parseInt(substrings[0]);       //число источников в сцене
            lights = new ArrayList<>(nl);
            for (int i = 0; i < nl; i++)
            {
                substrings = readLineAndSplit(br);
                double lx = Double.parseDouble(substrings[0]);
                double ly = Double.parseDouble(substrings[1]);
                double lz = Double.parseDouble(substrings[2]);
                int lr = Integer.parseInt(substrings[3]);
                int lg = Integer.parseInt(substrings[4]);
                int lb = Integer.parseInt(substrings[5]);

                if(lr < 0 || lr > 255 || lg < 0 || lg > 255 || lb < 0 || lb > 255)
                    throw new IOException("Wrong light color");

                lights.add(new Light(new Point3D(lx, ly, lz), new Color(lr, lg, lb)));
            }

            primitives = new ArrayList<>();
            while((substrings = readLineAndSplit(br)) != null)
            {
                String primitiveType = substrings[0];

                Primitive primitive = null;

                switch (primitiveType)
                {
                    case "SPHERE":
                        substrings = readLineAndSplit(br);
                        Point3D center = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));

                        substrings = readLineAndSplit(br);
                        double radius = Double.parseDouble(substrings[0]);

                        primitive = new Sphere(center, radius);
                        break;
                    case "BOX":
                        substrings = readLineAndSplit(br);
                        Point3D minPoint = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));

                        substrings = readLineAndSplit(br);
                        Point3D maxPoint = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));

                        primitive = new Box(minPoint, maxPoint);
                        break;
                }

                substrings = readLineAndSplit(br);
                double kDR = Double.parseDouble(substrings[0]);
                double kDG = Double.parseDouble(substrings[1]);
                double kDB = Double.parseDouble(substrings[2]);
                double kSR = Double.parseDouble(substrings[3]);
                double kSG = Double.parseDouble(substrings[4]);
                double kSB = Double.parseDouble(substrings[5]);
                double power = Double.parseDouble(substrings[6]);

                primitive.setOpticParameters(kDR, kDG, kDB, kSR, kSG, kSB, power);
                primitives.add(primitive);
            }


        }
        catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException | NullPointerException e)
        {
            return -1;
        }


        return 0;
    }

    public int loadRenderFile(File file)
    {

        isRenderFileLoaded = true;
        return 0;
    }

    public void drawWireFigures()
    {
    }


    private String[] readLineAndSplit(BufferedReader br) throws IOException
    {
        String line;
        String[] substrings;
        do {
            line = br.readLine();
            if(line == null)
                return null;
            line = line.substring(0, line.indexOf('/') != -1 ? line.indexOf('/') : line.length());
            substrings = line.split("\\s+");
        }
        while(substrings.length == 0 || "".equals(substrings[0]));
        return substrings;
    }
}
