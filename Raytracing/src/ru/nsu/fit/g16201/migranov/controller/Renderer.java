package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Light;
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

    private RendererWorker workers[][] = new RendererWorker[2][2];

    public Renderer(List<Primitive> primitives, List<Light> lights, Color ambientLightColor, Color backgroundColor, double gamma, int depth)
    {
        this.primitives = primitives;
        this.lights = lights;
        this.ambientLightColor = ambientLightColor;
        this.backgroundColor = backgroundColor;
        this.gamma = gamma;
        this.depth = depth;
    }

    public class RendererWorker implements Runnable {
        private int xStart;
        private int xEnd;
        private int yStart;
        private int yEnd;

        void RendererWorker(int xStart, int xEnd, int yStart, int yEnd)
        {
            this.xStart = xStart;
            this.xEnd = xEnd;
            this.yStart = yStart;
            this.yEnd = yEnd;
        }

        @Override
        public void run() {
            for(int i = yStart; i < yEnd; i++)
            {
                for(int j = xStart; j < xEnd; j++)
                {
                    
                }

            }

        }
    }

}
