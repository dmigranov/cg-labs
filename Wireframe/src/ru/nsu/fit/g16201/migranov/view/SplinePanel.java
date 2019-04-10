package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SplinePanel extends JPanel {
    private BufferedImage canvas;
    private Graphics canvasGraphics;

    public SplinePanel(int width, int height) {
        super();
        setPreferredSize(new Dimension(width, height));
    }


    @Override
    public void paintComponent(Graphics g)
    {

    }

}
