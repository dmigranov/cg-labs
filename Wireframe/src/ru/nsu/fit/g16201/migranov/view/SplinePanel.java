package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SplinePanel extends JPanel {
    private final int width, height;
    private BufferedImage canvas;
    private Graphics canvasGraphics;

    public SplinePanel(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.getGraphics();
        canvasGraphics.setColor(Color.BLACK);

    }


    @Override
    public void paintComponent(Graphics g)
    {

    }

}
