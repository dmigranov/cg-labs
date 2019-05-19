package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.*;
import ru.nsu.fit.g16201.migranov.model.primitives.Primitive;
import ru.nsu.fit.g16201.migranov.view.WireframePanel;

import javax.swing.*;
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

    double maxColor = 0;
    private WireframePanel panel;

    private List<Integer> [][][] grid;

    private FloatColor[][] floatColors;

    private ThreadPoolExecutor executor;

    private AtomicInteger pixelsCount;

    public Renderer(List<Primitive> worldPrimitives, List<Light> worldLights, Color ambientLightColor, WireframePanel panel)
    {
        this.worldPrimitives = worldPrimitives;
        this.worldLights = worldLights;  //todo
        this.ambientLightColor = ambientLightColor.getRGB();

        this.panel = panel;

    }

    public void render(int numberOfThreads, Color backgroundColor, double gamma, int depth, Point3D eye, Matrix viewMatrix, double zn, double sw, double sh, JLabel statusLabel)
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

        floatColors = new FloatColor[height][width];

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

        Runnable checker = () -> {
            while(true)
            {
                try {
                    if (executor.awaitTermination(300, TimeUnit.MILLISECONDS)) break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                statusLabel.setText("Progress: " + (double)pixelsCount.get()/width/height + "%");
            }

            for (int i = 0; i < height; i++)
            {
                for(int j = 0; j < width; j++)
                {
                    FloatColor fc = floatColors[i][j];
                    //todo: gamma

                    panel.setPixel(j, i, new Color((int) (fc.r / maxColor * 255), (int) (fc.g / maxColor * 255), (int) (fc.b / maxColor * 255)).getRGB());

                }
            }

            panel.repaint();
        };
        new Thread(checker).start();

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

            FloatColor color = trace(r0Initial, rdInitial);

            floatColors[picY][picX] = color;

            if(color.r > maxColor)
                maxColor = color.r;
            if(color.g > maxColor)
                maxColor = color.g;
            if(color.b > maxColor)
                maxColor = color.b;

            pixelsCount.incrementAndGet();
        }

        private FloatColor trace(Point3D r0, Point3D rd)
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
                return new FloatColor(backgroundColor);


            FloatColor reflectionColor = new FloatColor(0, 0, 0);
            if(currentDepth < depth) {
                currentDepth++;

                Point3D reflectionDir = null; //todo
                reflectionColor = trace(minIN.intersectionPoint, reflectionDir);
            }

            double[] diffuseAmbientCharacteristics = minDistancePrimitive.getDiffuseAmbientCharacteristics();
            double kADR = diffuseAmbientCharacteristics[0], kADG = diffuseAmbientCharacteristics[1], kADB = diffuseAmbientCharacteristics[2];
            double power = minDistancePrimitive.getPower();
            double IDSR = 0, IDSG = 0, IDSB = 0;   //diffuse & specular

            for(Light light : lights) {
                Point3D lightR0 = light.getCenter();
                Point3D lightDir = Point3D.subtract(minIN.intersectionPoint, lightR0).normalize();  //от источника к точке

                boolean noShadow = true;

                double lightDistance = Point3D.getDistanceSquare(lightR0, minIN.intersectionPoint);

                for (Primitive p : primitives) {        //проверяем, не находится ли точка в тени
                    IntersectionNormal in = findIntersection(p, lightR0, lightDir);
                    if(in != null && !p.equals(minDistancePrimitive) && Point3D.getDistanceSquare(lightR0, in.intersectionPoint) < lightDistance) {
                        noShadow = false;
                        break;
                    }
                }

                if(noShadow) {
                    Color color = light.getColor();

                    Point3D V = Point3D.subtract(eye, minIN.intersectionPoint);
                    Point3D H = Point3D.add(Point3D.getNegative(lightDir), V).normalize();

                    double fAtt = 1/(1 + lightDistance);
                    double scalarNL = Point3D.getScalarProduct(minIN.normalVector, Point3D.getNegative(lightDir));
                    if(scalarNL < 0)
                        scalarNL = 0;

                    double scalarNH = Point3D.getScalarProduct(minIN.normalVector, H);
                    if(scalarNH < 0)
                        scalarNH = 0;
                    scalarNH = Math.pow(scalarNH, power);

                    double scalar = (scalarNH + scalarNL) * fAtt;
                    IDSR += color.getRed() * kADR * scalar;
                    IDSG += color.getGreen() * kADG * scalar;
                    IDSB += color.getBlue() * kADB * scalar;

                }
            }

            //todo: отражения!
            double IR = (kADR * ((ambientLightColor & 0xFF0000) >> 16)) + IDSR;
            double IG = (kADG * ((ambientLightColor & 0x00FF00) >> 8)) + IDSG;
            double IB = (kADB * (ambientLightColor & 0x0000FF)) + IDSB;

            return new FloatColor(IR, IG, IB);
        }

        private IntersectionNormal findIntersection(Primitive p, Point3D r0, Point3D rd)
        {
            //todo: возможно переписать с использование таблиц...

            return p.findIntersection(r0, rd);
        }
    }

}
