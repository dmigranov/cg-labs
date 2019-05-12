package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.*;
import ru.nsu.fit.g16201.migranov.model.primitives.*;
import ru.nsu.fit.g16201.migranov.view.WireframePanel;

import java.awt.*;
import java.awt.event.*;
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

    private Color backgroundColor;
    private double gamma;
    private int depth;

    private boolean areRenderSettingsInitialized;
    //private boolean isBoxCalculated;

    private Matrix viewMatrix, projectionMatrix;
    private Point3D eye, ref, up;

    private WireframePanel wireframePanel;

    private Integer prevX = null, prevY = null;
    private double zn, zf, sw, sh;


    public Controller(WireframePanel wireframePanel) {
        this.wireframePanel = wireframePanel;

        wireframePanel.addMouseWheelListener(e -> {
            int count = e.getWheelRotation();

            if(e.isControlDown())
            {
                int dz = -count * 1;    //todo: вместо единицы какая-то дельта

                Matrix tr = Matrix.getTranslationMatrix(new Point3D(0, 0, dz));
                viewMatrix = Matrix.multiply(tr, viewMatrix);
                drawWireFigures();
            }
            else
            {
                //todo

            }
        });

        wireframePanel.setFocusable(true);
        wireframePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                double dx = 0, dy = 0, dz = 0;

                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT)
                    dx = 1;
                else if (key == KeyEvent.VK_RIGHT)
                    dx = -1;
                else if (key == KeyEvent.VK_UP)
                    dy = 1;
                else if (key == KeyEvent.VK_DOWN)
                    dy = -1;
                else if (key == KeyEvent.VK_P)
                    dz = 1;
                else if (key == KeyEvent.VK_M)
                    dz = -1;

                Matrix tr = Matrix.getTranslationMatrix(new Point3D(dx, dy, dz));
                viewMatrix = Matrix.multiply(tr, viewMatrix);
                drawWireFigures();

            }
        });

        wireframePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                if(!areRenderSettingsInitialized)
                    return;

                int x = e.getX();
                int y = e.getY();
                if(prevX != null) {
                    int dx = x - prevX;
                    int dy = y - prevY;

                    double xAngle = 0.01 * dx;
                    double yAngle = 0.01 * dy;

                    Matrix centerTranslate = Matrix.getTranslationMatrix(Point3D.getNegative(ref));
                    Matrix invertTranslate = Matrix.getTranslationMatrix(ref);

                    Matrix xRot = Matrix.getZRotateMatrix(-xAngle);
                    Matrix yRot = Matrix.getYRotateMatrix(yAngle);

                    //todo: по отдельности вращается сначала неплохо, но после смещени осей..

                    Matrix res = Matrix.multiply(invertTranslate, Matrix.multiply(Matrix.multiply(yRot, xRot), centerTranslate));

                    viewMatrix = Matrix.multiply(viewMatrix, res);

                    drawWireFigures();
                }
                prevX = x;
                prevY = y;
            }
        });

        wireframePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                prevX = null;
                prevY = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                wireframePanel.requestFocusInWindow();
            }
        });
    }

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
                    case "TRIANGLE":
                        substrings = readLineAndSplit(br);
                        Point3D tp1 = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));
                        substrings = readLineAndSplit(br);
                        Point3D tp2 = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));
                        substrings = readLineAndSplit(br);
                        Point3D tp3 = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));

                        primitive = new Triangle(tp1, tp2, tp3);
                        break;

                    case "QUADRANGLE":
                        substrings = readLineAndSplit(br);
                        Point3D qp1 = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));
                        substrings = readLineAndSplit(br);
                        Point3D qp2 = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));
                        substrings = readLineAndSplit(br);
                        Point3D qp3 = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));
                        substrings = readLineAndSplit(br);
                        Point3D qp4 = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));

                        primitive = new Quadrangle(qp1, qp2, qp3, qp4);
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
            areRenderSettingsInitialized = false;
            gamma = 1;
            backgroundColor = new Color(45, 60, 45);
            depth = 1;
        }
        catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException | NullPointerException e)
        {
            return -1;
        }

        return 0;
    }

    public int loadRenderFile(File file)
    {
        try(BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String[] substrings;

            substrings = readLineAndSplit(reader);
            int br = Integer.parseInt(substrings[0]);
            int bg = Integer.parseInt(substrings[1]);
            int bb = Integer.parseInt(substrings[2]);
            if(br < 0 || br > 255 || bg < 0 || bg > 255 || bb < 0 || bb > 255)
                throw new IOException("Wrong background color");
            this.backgroundColor = new Color(br, bg, bb);

            substrings = readLineAndSplit(reader);
            gamma = Double.parseDouble(substrings[0]);
            if(gamma < 0 || gamma > 10)
                throw new IOException("Wrong gamma");

            substrings = readLineAndSplit(reader);
            depth = Integer.parseInt(substrings[0]);
            if(depth <= 0)
                throw new IOException("Wrong depth");

            substrings = readLineAndSplit(reader);
            //todo: quality?

            substrings = readLineAndSplit(reader);
            eye = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));

            substrings = readLineAndSplit(reader);
            ref = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));

            substrings = readLineAndSplit(reader);
            up = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));

            substrings = readLineAndSplit(reader);
            zn = Double.parseDouble(substrings[0]);
            zf = Double.parseDouble(substrings[1]);

            substrings = readLineAndSplit(reader);
            sw = Double.parseDouble(substrings[0]);
            sh = Double.parseDouble(substrings[1]);

            viewMatrix = Matrix.getViewMatrix(eye, ref, up);
            projectionMatrix = Matrix.getProjectionMatrix(sw, sh, zf, zn);
        }
        catch (IOException | NullPointerException e)
        {
            return -1;
        }

        areRenderSettingsInitialized = true;
        return 0;
    }

    public void drawWireFigures()
    {
        if(!areRenderSettingsInitialized) //todo: + при нажатии init
        {
            double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE, minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
            up = new Point3D(0, 0, 1);
            System.out.println("reinited");
            for (Primitive primitive : primitives) {
                Point3D min = primitive.getMinPoint();
                Point3D max = primitive.getMaxPoint();

                minX = (min.x < minX ? min.x : minX);
                minY = (min.y < minY ? min.y : minY);
                minZ = (min.z < minZ ? min.z : minZ);

                maxX = (max.x > maxX ? max.x : maxX);
                maxY = (max.y > maxY ? max.y : maxY);
                maxZ = (max.z > maxZ ? max.z : maxZ);
            }
            //это же ref для матрицы view
            Point3D boxCenter = new Point3D((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
            this.ref = boxCenter;

            double addX = (maxX - minX) / 2 * 1.05;
            maxX = boxCenter.x + addX;
            minX = boxCenter.x - addX;
            double addY = (maxY - minY) / 2 * 1.05;
            maxY = boxCenter.y + addY;
            minY = boxCenter.y - addY;
            double addZ = (maxZ - minZ) / 2 * 1.05;
            maxZ = boxCenter.z + addZ;
            minZ = boxCenter.z - addZ;

            eye = new Point3D(minX - (maxY - minY) / 2 / Math.tan(Math.PI / 6), boxCenter.y, boxCenter.z);        //todo: провериьь x

            viewMatrix = Matrix.getViewMatrix(eye, ref, up);

            //todo: почему-то сильные искаженния (ошибка в матрице проекции?)!!!!!
            // !
            // !
            // !

            zn = (minX /*- eye.x*/) / 2;   //закомментил, хотя в задании написано. но в контексте матрицы проекции, когда уже применена view, eye.x в нуле!
            zf = maxX /*- eye.x*/ + (maxX - minX) / 2;

            sw = (maxZ - minZ)/*/Math.abs(zn)*/;    //todo: вписанность в экран!
            sh = (maxY - minY)/*/Math.abs(zn)*/;

            System.out.println(sw +  " " + sh);

            projectionMatrix = Matrix.getProjectionMatrix(sw, sh, zf, zn);

            areRenderSettingsInitialized = true;
        }
        //иначе - файл загружен, и матрицы proj и view уже заданы
        Matrix projView = Matrix.multiply(projectionMatrix, viewMatrix);

        wireframePanel.clear();

        WireframeLine l = primitives.get(0).getWireframeLines().get(0);
        for(Primitive primitive : primitives)
        {
            List<WireframeLine> lines = primitive.getWireframeLines();
            for (WireframeLine line : lines)
            {
                List<Point3D> points = line.getPoints();
                Point prev = null;    //пред точка в экранных координатах

                for(int i = 0; i < points.size(); i++)
                {
                    Point3D pointModel = points.get(i);
                    Matrix mpointModel = new Matrix(4, 1, pointModel.x, pointModel.y, pointModel.z, 1);
                    Matrix rpoint = Matrix.multiply(projView, mpointModel);

                    Point3D point = new Point3D(rpoint.get(0, 0), rpoint.get(1, 0), rpoint.get(2, 0));
                    double w = rpoint.get(3, 0);

                    if(point.z/w >= 0 && point.z/w <= 1) {
                        int x = (int)((point.x/w + 1)/2*wireframePanel.getWidth());
                        int y = (int)((point.y/w + 1)/2*wireframePanel.getHeight());

                        if(prev != null)
                        {
                            wireframePanel.drawLine(prev.x, prev.y, x, y);
                        }

                        prev = new Point(x, y);
                    }
                    else
                    {
                        //todo:2 подумать над тем, чтобы рисовать в таком случае линию, но не полностью!
                        prev = null;
                    }
                }

            }
        }
        wireframePanel.repaint();
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

    public void setAreRenderSettingsInitialized(boolean state) {
        areRenderSettingsInitialized = state;
    }

    public void recalculateProjectionParameters() {
        int width = wireframePanel.getWidth();
        int height = wireframePanel.getHeight();

        sw = sh * width/height;

        projectionMatrix = Matrix.getProjectionMatrix(sw, sh, zf, zn);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public double getGamma() {
        return gamma;
    }

    public int getDepth()
    {
        return depth;
    }

    public void setRenderingParameters(Color backgroundColor, double gamma, int depth) {
        this.backgroundColor = backgroundColor;
        this.gamma = gamma;
        this.depth = depth;
    }
}
