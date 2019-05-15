package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Light;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.model.primitives.Primitive;

import java.awt.*;
import java.util.List;

public class Renderer {
    private final List<Primitive> primitives;
    private final List<Light> lights;
    private final Color ambientLightColor;
    private final Color backgroundColor;
    private final double gamma;
    private final int depth;
    private final Point3D eye;
    private final double zn;
    private final double sw;
    private final double sh;

    private RendererWorker workers[][] = new RendererWorker[2][2];

    public Renderer(List<Primitive> primitives, List<Light> lights, Color ambientLightColor, Color backgroundColor, double gamma, int depth, Point3D eye, double zn, double sw, double sh)
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
    }

    public class RendererWorker implements Runnable {
        private int xStartIter;
        private int xEndIter;
        private int yStartIter;
        private int yEndIter;

        void RendererWorker(int xStartIter, int xEndIter, int yStartIter, int yEndIter)
        {
            this.xStartIter = xStartIter;
            this.xEndIter = xEndIter;
            this.yStartIter = yStartIter;
            this.yEndIter = yEndIter;
        }

        @Override
        public void run() {
            //y

            double nearStartX = eye.x - sw/2;
            double nearEndX = eye.x + sw/2;
            double nearStartY = eye.y - sh/2;
            double nearEndY = eye.y + sh/2;

            for(int i = yStartIter; i < yEndIter; i++)
            {

                for(int j = xEndIter; j < xEndIter; j++)
                {

                }

            }

        }
    }

}
