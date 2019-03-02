package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private BufferedImage image = null;
    public void setImage(BufferedImage image)
    {
        int realWidth = image.getWidth();
        int realHeight = image.getHeight();
        //todo stack overflow how to resize the buffered image graphics 2d
    }

}
