package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.view.ImagePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Controller {
    private ImagePanel originalImagePanel, modifiableImagePanel, modifiedImagePanel;
    private BufferedImage originalImage;        //ПОЛНОЕ изображение

    private static int[][] orderedDitherMatrix = {{0,8,2,10}, {12,4,14,6}, {3,11,1,9}, {15,7,13,5}};
    private static double[][] sharpnessMatrix = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};
    private static double[][] simpleBlurMatrix = {{0, 1/6.0, 0}, {1/6.0, 1/3.0, 1/6.0}, {0, 1/6.0, 0}};
    private static double[][] embossingMatrix = {{0, 1, 0}, {-1, 0, 1}, {0, -1, 0}};


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
        //todo: исправить! цвета и т.д
        int matrixSize = 4;
        double[][] orderedDitherDoubleMatrix = new double[4][4];
        for(int i  = 0; i < matrixSize; i++)
            for(int j  = 0; j < matrixSize; j++)
                orderedDitherDoubleMatrix[i][j] = 1.0 * orderedDitherMatrix[i][j]/(matrixSize * matrixSize);

        int rCount = 256/rLevel, gCount = 256/gLevel, bCount = 256/bLevel;
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

                //int addition = (int)((256.0/(4 - 1))*(orderedDitherDoubleMatrix[j][i]/(4.0 * 4.0) - 0.5)); //-1/2, r wiki
                int addition = orderedDitherMatrix[j][i];


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

    private void applyConvolutionMatrix(double[][] convolutionMatrix, BufferedImage source)
    {
        //todo: не раюотает акварель потому что мы изменяем и читаем одну матрицу! надо клонировать!!!!!!
        int fy = convolutionMatrix.length;
        int fx = convolutionMatrix[0].length;

        BufferedImage image = source;   //вот тут надо клон а не оригинал
        int height = image.getHeight();
        int width = image.getWidth();

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                double rsum = 0, gsum = 0, bsum = 0;
                for (int i = -fy/2; i < fy/2 + 1; i++)
                {
                    for (int j = -fx/2; j < fx/2 + 1; j++)
                    {

                        double convolutionMatrixValue = convolutionMatrix[i + fy/2][j + fy/2];
                        int color;
                        if (j + x >= 0 && j + x < width && i + y >= 0 && i + y < height)
                            color = image.getRGB(j + x, i + y);
                        else
                            continue; //todo: возможно, продлить изображение по краям. сейчас по сути по нулям. можно оптимизировать
                        int red = (color & 0xFF0000) >> 16;
                        int green = (color & 0x00FF00) >> 8;
                        int blue = color & 0x0000FF;
                        rsum += red * convolutionMatrixValue;
                        gsum += green * convolutionMatrixValue;
                        bsum += blue * convolutionMatrixValue;
                    }
                }
                int r = saturate((int)Math.round(rsum));
                int g = saturate((int)Math.round(gsum));
                int b = saturate((int)Math.round(bsum));
                //System.out.println(x + " " + y + " " + r + " " + g + " " + b);
                modifiedImagePanel.setColor(x, y, b + (g << 8) + (r << 16));
            }
        }
    }

    public void applySharpnessFilter() {
        applyConvolutionMatrix(sharpnessMatrix, modifiableImagePanel.getImage());
        modifiedImagePanel.repaint();
    }

    public void applySimpleBlur() {
        applyConvolutionMatrix(simpleBlurMatrix ,modifiableImagePanel.getImage());
        modifiedImagePanel.repaint();
    }

    public void applyEmbossing() {
        //todo: как правильно?
        applyConvolutionMatrix(embossingMatrix, modifiableImagePanel.getImage());
        BufferedImage image = modifiedImagePanel.getImage();
        for(int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                int color = image.getRGB(x, y);
                int red = (color & 0xFF0000) >> 16;
                int green = (color & 0x00FF00) >> 8;
                int blue = color & 0x0000FF;
                red = saturate(red + 128);
                green = saturate(green + 128);
                blue = saturate(blue + 128);

                int newColor = blue + (green << 8) + (red << 16);   //todo: |
                image.setRGB(x, y, newColor);
            }
        }
        modifiedImagePanel.repaint();
    }

    public void applyWatercolor()
    {
        applyMedianFilter(modifiableImagePanel.getImage());
    }

    private void applyMedianFilter(BufferedImage source)
    {
        int fy = 5;
        int fx = 5;

        BufferedImage image = source;
        int height = image.getHeight();
        int width = image.getWidth();

        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                List<Integer> neighbours = new ArrayList<>();
                for (int i = -fy / 2; i < fy / 2 + 1; i++) {
                    for (int j = -fx / 2; j < fx / 2 + 1; j++) {
                        if (j + x >= 0 && j + x < width && i + y >= 0 && i + y < height)
                            neighbours.add(image.getRGB(x + j, y + i));
                    }
                }
                Collections.sort(neighbours);
                modifiedImagePanel.setColor(x, y, neighbours.get(neighbours.size() / 2));
            }
        }
        applyConvolutionMatrix(sharpnessMatrix, modifiedImagePanel.getImage());
        modifiedImagePanel.repaint();
    }
}