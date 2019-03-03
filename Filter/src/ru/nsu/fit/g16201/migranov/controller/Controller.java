package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.view.ImagePanel;

import javax.imageio.ImageIO;
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
        }
        catch (IOException e)
        {
            //todo диалог
        }

    }

    public void invert() {
        /*for(int y = 0; y < modifiableImagePanel.getHeight(); y++)
        {
            for(int x = 1; x <= modifiableImagePanel.getWidth(); x++)
            {
                modifiedImagePanel.setColor(x, y, 0xFFFFFF - modifiableImagePanel.getColor(x, y));
            }
        }*/
    }
}
