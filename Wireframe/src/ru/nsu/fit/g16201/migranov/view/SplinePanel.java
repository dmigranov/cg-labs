package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SplinePanel extends JPanel {
    private final int width, height;
    private BufferedImage canvas;
    private Graphics2D canvasGraphics;

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
        canvasGraphics.drawLine(0, 0, 400, 400);
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
        canvasGraphics.drawLine(u, v, u, v);
    }
}
