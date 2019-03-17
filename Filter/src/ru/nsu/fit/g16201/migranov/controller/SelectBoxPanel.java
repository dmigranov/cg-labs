package ru.nsu.fit.g16201.migranov.controller;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class SelectBoxPanel extends JPanel {
    private Border border, emptyBorder;
    private boolean borderHasToBeDrawn = false, isInitialized = false;
    public SelectBoxPanel() {


    }

    /*@Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.setXORMode(Color.white);
        borderHasToBeDrawn = true;
        paintBorder(g);
        borderHasToBeDrawn = false;

        g.setPaintMode();

    }*/
    @Override
    public void paintBorder(Graphics g)
    {
        System.out.println(getX() + " " + getY() + " " + getWidth() + " " +getHeight());

        g.setXORMode(Color.WHITE);

        super.paintBorder(g);
        //getBorder().paintBorder(this, g, 0, 0, getWidth(), getHeight());
        //g.setPaintMode();

    }

}
