package ru.nsu.fit.g16201.migranov.controller;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class SelectBoxPanel extends JPanel {

    @Override
    public void paintBorder(Graphics g)
    {
        g.setXORMode(Color.WHITE);

        //super.paintBorder(g);
        //getBorder().paintBorder(this, g, 0, 0, getWidth(), getHeight());
        super.paintBorder(g);


    }

}
