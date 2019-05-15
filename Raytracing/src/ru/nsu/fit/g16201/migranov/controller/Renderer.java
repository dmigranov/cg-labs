package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Light;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.model.primitives.Primitive;
import ru.nsu.fit.g16201.migranov.view.WireframePanel;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Renderer {
    private List<Primitive> primitives;
    private List<Light> lights;
    private Color ambientLightColor;
    private Color backgroundColor;
    private double gamma;
    private int depth;
    private Point3D eye;
    private double zn;
    private double sw;
    private double sh;
    private WireframePanel panel;

    private ThreadPoolExecutor executor;

    public Renderer(List<Primitive> primitives, List<Light> lights, Color ambientLightColor, Color backgroundColor, double gamma, int depth, Point3D eye, double zn, double sw, double sh, WireframePanel panel)
    {
        this.primitives = primitives;
        this.lights = lights;
        this.ambientLightColor = ambientLightColor;
        this.backgroundColor = backgroundColor;
        this.gamma = gamma;
        this.depth = depth;
        this.eye = eye;
        this.zn = zn;
        this.sw = sw;
        this.sh = sh;
        this.panel = panel;
    }

    public void render(int numberOfThreads)
    {
        int width = panel.getWidth();
        int height = panel.getHeight(); //в пискелах


        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(width*height));

        double nearStartX = eye.x - sw/2;
        double nearEndX = eye.x + sw/2;
        double nearStartY = eye.y - sh/2;
        double nearEndY = eye.y + sh/2;

        for(int i = yStartIter; i < yEndIter; i++)
        {

            for(int j = xStartIter; j < xEndIter; j++)
            {

            }

        }

    }

    public class RendererTask implements Runnable {
        private int xStartIter;
        private int xEndIter;
        private int yStartIter;
        private int yEndIter;

        RendererTask(int xStartIter, int xEndIter, int yStartIter, int yEndIter)  //не включчая концы
        {
            this.xStartIter = xStartIter;
            this.xEndIter = xEndIter;
            this.yStartIter = yStartIter;
            this.yEndIter = yEndIter;
        }

        @Override
        public void run() {
            //y




            //double startX = ;



        }
    }

}
