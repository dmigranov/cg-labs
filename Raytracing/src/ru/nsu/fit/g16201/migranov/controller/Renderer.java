package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.IntersectionNormal;
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
import java.util.concurrent.atomic.AtomicInteger;

public class Renderer {
    private List<Primitive> worldPrimitives, primitives;
    private List<Light> worldLights, lights;
    //private Color ambientLightColor, backgroundColor;
    private int ambientLightColor, backgroundColor;
    private double gamma;
    private int depth;
    private Point3D eye;
    private Matrix viewMatrix;
    private double zn;
    private double sw;
    private double sh;
    private WireframePanel panel;

    private List<Integer> [][][] grid;

    private ThreadPoolExecutor executor;

    private AtomicInteger pixelsCount;

    public Renderer(List<Primitive> worldPrimitives, List<Light> worldLights, Color ambientLightColor, WireframePanel panel)
    {
        this.worldPrimitives = worldPrimitives;
        this.worldLights = worldLights;  //todo
        this.ambientLightColor = ambientLightColor.getRGB();

        this.panel = panel;

    }

    public void render(int numberOfThreads, Color backgroundColor, double gamma, int depth, Point3D eye, Matrix viewMatrix, double zn, double sw, double sh)
    {
        this.backgroundColor = backgroundColor.getRGB();
        this.gamma = gamma;
        this.depth = depth;
        this.eye = eye;
        this.viewMatrix = viewMatrix;
        this.zn = zn;
        this.sw = sw;
        this.sh = sh;

        int width = panel.getWidth();
        int height = panel.getHeight(); //в пикселах

        pixelsCount = new AtomicInteger(0);

        primitives = new ArrayList<>(worldPrimitives.size());
        for(Primitive worldPrimitive : worldPrimitives) {
            primitives.add(worldPrimitive.movePrimitive(viewMatrix));
        }

        lights = new ArrayList<>(worldLights.size());
        for(Light worldLight : worldLights) {
            lights.add(new Light(worldLight, viewMatrix));
        }

        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(width*height));

        double nearStartX = - sw/2;
        double nearStartY = - sh/2;

        double dx = sw/width, dy = sh/height;
        double x;
        double y = nearStartY + dy/2;

        for(int i = 0; i < height; i++)
        {
            x = nearStartX + dx/2;
            for(int j = 0; j < width; j++)
            {
                //центры пикселя
                executor.execute(new RendererTask(x, y, j, i));
                x += dx;
            }
            y += dy;
        }
        executor.shutdown();

        while(true)
        {
            try {
                if (executor.awaitTermination(2, TimeUnit.SECONDS)) break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //todo: статус бар
        }

        panel.repaint();

    }

    public class RendererTask implements Runnable {
        private double pixelX;
        private double pixelY;
        private int picX;
        private int picY;

        private int currentDepth = 1;

        //одно легко высчисляется из другого, но время деньги
        RendererTask(double pixelX, double pixelY, int picX, int picY)
        {
            this.pixelX = pixelX;
            this.pixelY = pixelY;
            this.picX = picX;
            this.picY = picY;
        }

        @Override
        public void run() {
            //луч = R0(x0, y0, z0), Rdirection(xd, yd, zd)
            Point3D r0Initial = new Point3D(0, 0, 0);
            Point3D rdInitial = new Point3D(pixelX, pixelY, zn).normalize();

            int color = trace(r0Initial, rdInitial);

            panel.setPixel(picX, picY, color);  //todo: на самом деле сложить в массив и провести гамма коррекцию

            pixelsCount.incrementAndGet();
        }

        private double trace(Point3D r0, Point3D rd)
        {
            double minDistance = Double.MAX_VALUE;
            Primitive minDistancePrimitive = null;
            IntersectionNormal minIN = null;

            for (Primitive p : primitives) {
                IntersectionNormal in = findIntersection(p, r0, rd);
                if (in != null) {
                    double distance = Point3D.getDistanceSquare(r0, in.intersectionPoint);
                    if (distance < minDistance) {
                        minDistance = distance;
                        minDistancePrimitive = p;
                        minIN = in;
                    }
                }
            }

            if(minDistancePrimitive == null)
                return backgroundColor;

            if(currentDepth < depth) {
                currentDepth++;

                Point3D reflectionDir = null; //todo
                double color = trace(minIN.intersectionPoint, reflectionDir);
            }



            double[] diffuseAmbientCharacteristics = minDistancePrimitive.getDiffuseAmbientCharacteristics();
            double kAR = diffuseAmbientCharacteristics[0], kAG = diffuseAmbientCharacteristics[1], kAB = diffuseAmbientCharacteristics[2];

            double IR = (kAR * ((ambientLightColor & 0xFF0000) >> 16));
            double IG = (kAG * ((ambientLightColor & 0x00FF00) >> 8));
            double IB = (kAR * (ambientLightColor & 0x0000FF));

            return new Color(IR, IG, IB).getRGB();
        }

        private IntersectionNormal findIntersection(Primitive p, Point3D r0, Point3D rd)
        {
            //todo: возможно переписать с использование таблиц...

            return p.findIntersection(r0, rd);
        }
    }

}
