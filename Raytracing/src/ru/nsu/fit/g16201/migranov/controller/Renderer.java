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

}
