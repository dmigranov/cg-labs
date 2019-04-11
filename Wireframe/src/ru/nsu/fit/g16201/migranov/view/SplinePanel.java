package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SplinePanel extends JPanel {
    private final int width, height;
    private BufferedImage canvas;
    private Graphics2D canvasGraphics;

    private final int splinePointRadius = 3;
    private final Color splinePointColor = Color.BLACK;
    private final Color splineLineColor = Color.GRAY;


    public SplinePanel(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.createGraphics();
        canvasGraphics.setColor(Color.BLACK);
        canvasGraphics.setBackground(Color.WHITE);

        canvasGraphics.clearRect(0, 0, width, height);
        canvasGraphics.drawLine(width/2, 0, width/2, height - 1);
        canvasGraphics.drawLine(0, height/2, width - 1, height/2);

        //оси

    }


    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, width, height, null);
    }

    public void drawPoint(int u, int v) {
        //canvas.setRGB(u, v, 0);
        canvasGraphics.setColor(splineLineColor);
        canvasGraphics.drawLine(u, v, u, v);
    }

    public void drawSplinePoint(int u, int v)
    {
        canvasGraphics.setColor(splinePointColor);
        canvasGraphics.fillOval( u - splinePointRadius, v - splinePointRadius, splinePointRadius*2, splinePointRadius*2);
    }

    public int getSplinePointRadius()
    {
        return splinePointRadius;
    }

    public int getSplinePointColor() {
        return splinePointColor.getRGB();
    }

    public int getRGB(int x, int y) {
        return canvas.getRGB(x, y);
    }
}
