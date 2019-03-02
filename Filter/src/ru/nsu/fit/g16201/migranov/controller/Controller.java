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
        }
        catch (IOException e)
        {
            //todo диалог
        }

    }
}
