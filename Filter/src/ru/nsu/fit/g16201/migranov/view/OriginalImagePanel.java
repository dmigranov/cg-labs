package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;

public class OriginalImagePanel extends ImagePanel {
    private JPanel selectBox;
    /*public OriginalImagePanel(JPanel selectBox)
    {
        this.selectBox = selectBox;
    }*/
    public void setSelectBox(JPanel selectBox)
    {
        this.selectBox = selectBox;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(image != null)
            //g.drawImage(image, 1, 1, image.getWidth(), image.getHeight(), null);
            g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        if(selectBox != null)
            selectBox.revalidate();

    }
}
