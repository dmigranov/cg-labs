package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.view.ImagePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {
    private ImagePanel originalImagePanel, modifiableImagePanel, modifiedImagePanel;
    private BufferedImage originalImage;        //ПОЛНОЕ изображение

    private static int[][] orderedDitherMatrix = {{0,8,2,10}, {12,4,14,6}, {3,11,1,9}, {15,7,13,5}};

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
                int newColor = Y + (Y << 8) + (Y << 16);    //todo: |

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

    public void doOrderedDithering(int rLevel, int gLevel, int bLevel)  //todo: добавить размер матрицы
    {
        int rCount = 256/rLevel, gCount = 256/gLevel, bCount = 256/bLevel;
        //cs.princeton.edu
        for(int y = 0; y < modifiableImagePanel.getImageHeight(); y++)
        {
            int j = y % 4;
            for(int x = 0; x < modifiableImagePanel.getImageWidth(); x++)
            {
                int i = x % 4;
                int color = modifiableImagePanel.getColor(x, y);
                int red = (color & 0xFF0000) >> 16, green = (color & 0x00FF00) >> 8, blue = color & 0x0000FF;
                int nred = 0, ngreen = 0, nblue = 0;

                //red >>= 5; //оставляем три старших бит
                //green >>= 5;
                //blue >>= 6;
                //int threshold = orderedDitherMatrix[j][i] * 255 / 16;

                int addition = orderedDitherMatrix[j][i]; //-1/2, r wiki

                nred = red + addition;
                ngreen = green + addition;
                nblue = blue + addition;

                nred = nred/rCount*rCount;
                ngreen = ngreen/gCount*gCount;
                nblue = nblue/ bCount*bCount;

                int newColor = nblue + (ngreen << 8) + (nred << 16);
                modifiedImagePanel.setColor(x, y, newColor);
            }
        }
        modifiedImagePanel.repaint();
    }

}
