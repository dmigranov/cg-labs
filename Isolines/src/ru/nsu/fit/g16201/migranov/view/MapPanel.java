package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MapPanel extends JPanel {
    private BufferedImage canvas;
    private Graphics2D canvasGraphics;
    private BufferedImage gridCanvas;
    private Graphics2D gridGraphics;
    private int width, height;

    MapPanel(int width, int height)
    {
        setLayout(new FlowLayout());
        this.width = width;
        this.height = height;
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.createGraphics();
        canvasGraphics.setColor(Color.BLACK);

        gridCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        gridGraphics = gridCanvas.createGraphics();
        gridGraphics.setColor(Color.BLACK);
        gridGraphics.setBackground(new Color(0,0,0,0));

    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, width, height, null);
        g.drawImage(gridCanvas, 0, 0, width, height, null);
    }

    public void drawLine(int x1, int y1, int x2, int y2)
    {
        canvasGraphics.drawLine(x1,y1,x2,y2);
        repaint();
    }

    public void drawGridLine(int x1, int y1, int x2, int y2)
    {
        gridGraphics.drawLine(x1,y1,x2,y2);
        repaint();
    }

    public void clearGrid()
    {
        gridGraphics.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());
    }


    public void clear() {
        canvasGraphics.setBackground(new Color(0,0,0,0));

        canvasGraphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
