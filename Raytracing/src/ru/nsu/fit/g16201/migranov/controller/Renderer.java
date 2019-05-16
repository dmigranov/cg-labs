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
    private Color ambientLightColor, backgroundColor;
    private double gamma;
    private int depth;
    private Point3D eye;
    private double zn, sw, sh;
    private WireframePanel panel;

    private ThreadPoolExecutor executor;

    public Renderer(List<Primitive> primitives, List<Light> lights, Color ambientLightColor, WireframePanel panel)
    {
        this.primitives = primitives;
        this.lights = lights;
        this.ambientLightColor = ambientLightColor;

        this.panel = panel;

    }

    public void render(int numberOfThreads, Color backgroundColor, double gamma, int depth, Point3D eye, double zn, double sw, double sh)
    {
        this.backgroundColor = backgroundColor;
        this.gamma = gamma;
        this.depth = depth;
        this.eye = eye;
        this.zn = zn;
        this.sw = sw;
        this.sh = sh;

        int width = panel.getWidth();
        int height = panel.getHeight(); //в пискелах


        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(width*height));

        //это неправильно! это ведь ещё не в координатах камеры
        //по нулям, буду делать в ккординатах камеры!
        double nearStartX = eye.x - sw/2;
        double nearEndX = eye.x + sw/2;
        double nearStartY = eye.y - sh/2;
        double nearEndY = eye.y + sh/2;


        double dx = sw/width/2, dy = sh/height/2;
        double x = nearStartX + dx;
        double y = nearStartY + dy;


        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                //центры пикселя
                executor.execute(new RendererTask(x, y, i, j));

                x += dx;
            }

            y += dy;
        }

    }

    public class RendererTask implements Runnable {
        private double pixelX;
        private double pixelY;
        private int picX;
        private int picY;

        //одно легко высчисляется из другого, но время деньги
        RendererTask(double pixelX, double pixelY, int picX, int picY)
        {
            this.pixelX = pixelX;
            this.pixelY = pixelY;
            this.picX = picX;
            this.picY = picY;

            //todo: перейти в координаты камеры
            //луч = R0(x0, y0, z0), Rdirection(xd, yd, zd)
            Point3D r0 = eye;
            //Point3D rd =


        }



        @Override
        public void run() {
            for (Primitive p : primitives)
            {

            }

        }
    }

}
