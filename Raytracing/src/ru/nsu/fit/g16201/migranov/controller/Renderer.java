package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Light;
import ru.nsu.fit.g16201.migranov.model.Matrix;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.model.primitives.Primitive;
import ru.nsu.fit.g16201.migranov.view.WireframePanel;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Renderer {
    private List<Primitive> worldPrimitives, primitives;
    private List<Light> worldLights, lights;
    private Color ambientLightColor, backgroundColor;
    private double gamma;
    private int depth;
    private Point3D eye;
    private Matrix viewMatrix;
    private double zn;
    private double sw;
    private double sh;
    private WireframePanel panel;

    private ThreadPoolExecutor executor;

    public Renderer(List<Primitive> worldPrimitives, List<Light> worldLights, Color ambientLightColor, WireframePanel panel)
    {
        this.worldPrimitives = worldPrimitives;
        this.worldLights = worldLights;  //todo
        this.ambientLightColor = ambientLightColor;

        this.panel = panel;

    }

    public void render(int numberOfThreads, Color backgroundColor, double gamma, int depth, Point3D eye, Matrix viewMatrix, double zn, double sw, double sh)
    {
        this.backgroundColor = backgroundColor;
        this.gamma = gamma;
        this.depth = depth;
        this.eye = eye;
        this.viewMatrix = viewMatrix;
        this.zn = zn;
        this.sw = sw;
        this.sh = sh;

        int width = panel.getWidth();
        int height = panel.getHeight(); //в пикселах

        primitives = new ArrayList<>(worldPrimitives.size());
        for(Primitive worldPrimitive : worldPrimitives) {
            primitives.add(worldPrimitive.movePrimitive(viewMatrix));
        }

        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(width*height));

        //это неправильно! это ведь ещё не в координатах камеры
        //по нулям, буду делать в ккординатах камеры!
        double nearStartX = - sw/2;
        double nearEndX = sw/2;
        double nearStartY = - sh/2;
        double nearEndY = sh/2;

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
