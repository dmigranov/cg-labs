package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.view.ImagePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {
    private ImagePanel originalImagePanel, modifiableImagePanel, modifiedImagePanel;
    private BufferedImage originalImage;        //ПОЛНОЕ изображение
    public Controller(ImagePanel originalImagePanel, ImagePanel modifiableImagePanel, ImagePanel modifiedImagePanel) {
        this.originalImagePanel = originalImagePanel;
        this.modifiableImagePanel = modifiableImagePanel;
        this.modifiedImagePanel = modifiedImagePanel;
    }

    public void openImage(File file)
    {
        try {
            BufferedImage image = ImageIO.read(file);
            this.originalImage = image;
            originalImagePanel.setImage(image);
            modifiableImagePanel.setImage(image);   //todo: потом изменить на выбор квадратом
            modifiedImagePanel.setEmptyImage(modifiableImagePanel.getWidth(), modifiableImagePanel.getHeight());
        }
        catch (IOException e)
        {
            //todo диалог
        }

    }

    public void invert() {
        for(int y = 0; y < modifiableImagePanel.getImageHeight(); y++)
        {
            for(int x = 0; x < modifiableImagePanel.getImageWidth(); x++)
            {
                modifiedImagePanel.setColor(x, y, 0xFFFFFF - modifiableImagePanel.getColor(x, y));
            }
        }
        modifiedImagePanel.repaint();
    }

    public void desaturate() {
        for(int y = 0; y < modifiableImagePanel.getImageHeight(); y++)
        {
            for(int x = 0; x < modifiableImagePanel.getImageWidth(); x++)
            {
                int color = modifiableImagePanel.getColor(x, y);
                int red = (color & 0xFF0000) >> 16;
                int green = (color & 0x00FF00) >> 8;
                int blue = color & 0x0000FF;
                int Y = (int)(0.299 * red + 0.587 * green + 0.114 * blue);
                Y = saturate(Y);
                int newColor = Y + (Y << 8) + (Y << 16);
                Color c = new Color(Y,Y,Y);

                modifiedImagePanel.setColor(x, y, newColor);
            }
        }
        modifiedImagePanel.repaint();
    }

    private int saturate(int v)
    {
        if (v > 255)
            return 255;
        else if (v < 0)
            return 0;
        return v;
    }
}
