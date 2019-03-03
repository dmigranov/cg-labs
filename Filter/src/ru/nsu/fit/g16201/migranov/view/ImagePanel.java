package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private BufferedImage image = null;
    //private int width, height;


    public void setImage(BufferedImage newImage)
    {
        int realWidth = newImage.getWidth();
        int realHeight = newImage.getHeight();

        int desiredWidth = 350;
        int desiredHeight = 350;

        if(realWidth <= desiredWidth && realHeight <= desiredHeight)
            image = newImage;
        else
        {
            if (realWidth * desiredHeight < realHeight * desiredWidth)
                desiredWidth = realWidth * desiredHeight / realHeight;
            else
                desiredHeight = realHeight * desiredWidth / realWidth;
            image = new BufferedImage(desiredWidth, desiredHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            //stack overflow how to resize buffered image
            //почему он так плохо ресайзит? TODO: спросить, самому ли реализовывать интерполяцию при загрузке
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(newImage, 0, 0, desiredWidth, desiredHeight, null);
            g.dispose();


            //width = desiredWidth;
            //height = desiredHeight;
        }


        repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(image != null)
            g.drawImage(image, 1, 1, image.getWidth(), image.getHeight(), null);
    }

    /*public int getColor(int x, int y)
    {
        //image.getRGB([])  - можно сразу много получать?? а можно сразу весь массив спросить!
        return image.getRGB(x ,y);
    }

    public void setColor(int x, int y, int color)
    {
        //image.setRGB([])  - можно сразу много получать?? а можно сразу весь массив спросить!
        image.setRGB(x ,y, color);
    }

    @Override
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }*/
}
