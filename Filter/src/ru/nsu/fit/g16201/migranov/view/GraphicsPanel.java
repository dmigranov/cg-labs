package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicsPanel extends JPanel {
    private BufferedImage canvas;
    private Graphics canvasGraphics;
    private int width, height;
    GraphicsPanel(int width, int height)
    {
        this.width = width;
        this.height = height;
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.getGraphics();
        canvasGraphics.setColor(Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, width, height, null);
    }

    public void drawLine(int x1, int y1, int x2, int y2)
    {
        canvasGraphics.drawLine(x1,y1,x2,y2);
        repaint();
    }
    public void drawLine(int x1, int y1, int x2, int y2, int color)
    {
        canvasGraphics.setColor(new Color(color));
        canvasGraphics.drawLine(x1,y1,x2,y2);
        repaint();
    }
}
