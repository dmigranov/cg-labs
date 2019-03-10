package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.view.ImagePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.abs;

public class Controller {
    private ImagePanel originalImagePanel, modifiableImagePanel, modifiedImagePanel;
    private JPanel selectBox;
    private BufferedImage originalImage;        //ПОЛНОЕ изображение

    private static int[][] orderedDitherMatrix = {{0,8,2,10}, {12,4,14,6}, {3,11,1,9}, {15,7,13,5}};
    private static double[][] sharpnessMatrix = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};
    private static double[][] simpleBlurMatrix = {{0, 1/6.0, 0}, {1/6.0, 1/3.0, 1/6.0}, {0, 1/6.0, 0}};
    private static double[][] embossingMatrix = {{0, 1, 0}, {-1, 0, 1}, {0, -1, 0}};
    private static double[][] sobelXMatrix = {{-1,0,1}, {-2,0,2}, {-1,0,1}};
    private static double[][] sobelYMatrix = {{-1,-2,-1}, {0,0,0}, {1,2,1}};



    public Controller(ImagePanel originalImagePanel, ImagePanel modifiableImagePanel, ImagePanel modifiedImagePanel) {
        this.originalImagePanel = originalImagePanel;
        this.modifiableImagePanel = modifiableImagePanel;
        this.modifiedImagePanel = modifiedImagePanel;

        originalImagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
    }

    public void openImage(File file)
    {
        try {
            BufferedImage image = ImageIO.read(file);
            if(image == null)
                throw new IOException();
            this.originalImage = image;
            originalImagePanel.setImage(image);
            modifiableImagePanel.setImage(image);   //todo: потом изменить на выбор квадратом
            modifiedImagePanel.setEmptyImage(modifiableImagePanel.getWidth(), modifiableImagePanel.getHeight());
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, "Could not read this file", "Error", JOptionPane.ERROR_MESSAGE);
            //todo: возможно, стоить снова поставить Untitled Заголовок окошка
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
        modifiedImagePanel.setImage(getDesaturatedImage(modifiableImagePanel.getImage()));
    }

    private BufferedImage getDesaturatedImage(BufferedImage source)
    {
        BufferedImage returnImage = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        for(int y = 0; y < source.getHeight(); y++)
        {
            for(int x = 0; x < source.getWidth(); x++)
            {
                int color = source.getRGB(x, y);
                int red = (color & 0xFF0000) >> 16;
                int green = (color & 0x00FF00) >> 8;
                int blue = color & 0x0000FF;
                int Y = (int)(0.299 * red + 0.587 * green + 0.114 * blue);
                Y = saturate(Y);
                //int newColor = Y + (Y << 8) + (Y << 16);
                returnImage.setRGB(x, y, getColorFromComponents(Y, Y, Y));
            }
        }
        return returnImage;
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

    //source != modifiedImage!
    private void applyConvolutionMatrix(double[][] convolutionMatrix, BufferedImage source)
    {
        int fy = convolutionMatrix.length;
        int fx = convolutionMatrix[0].length;

        BufferedImage image = source;
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
                        {
                            int nx = x + j, ny = y + i;
                            if(nx < 0)
                                nx = 0;
                            else if (nx >= width)
                                nx = width - 1;
                            if(ny < 0)
                                ny = 0;
                            else if (ny >= height)
                                ny = height - 1;
                            color = image.getRGB(nx, ny);
                        }
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
                //modifiedImagePanel.setColor(x, y, b + (g << 8) + (r << 16));
                modifiedImagePanel.setColor(x, y, getColorFromComponents(r, g, b));
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
                //int newColor = blue + (green << 8) + (red << 16);
                image.setRGB(x, y, getColorFromComponents(red, green, blue));
            }
        }
        modifiedImagePanel.repaint();
    }

    public void applyWatercolor()
    {
        //applyMedianFilter(modifiableImagePanel.getImage()         //just median filter
        applyConvolutionMatrix(sharpnessMatrix, applyMedianFilter(modifiableImagePanel.getImage()));
        modifiedImagePanel.repaint();
    }

    private BufferedImage applyMedianFilter(BufferedImage source)
    {
        int fy = 5;
        int fx = 5;

        BufferedImage image = source;
        int height = image.getHeight();
        int width = image.getWidth();
        BufferedImage returnImage = new BufferedImage(width, height, image.getType());

        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //List<Integer> neighbours = new ArrayList<>();
                List<Integer> rneighbours = new ArrayList<>(), gneighbours = new ArrayList<>(), bneighbours = new ArrayList<>();
                for (int i = -fy / 2; i < fy / 2 + 1; i++) {
                    for (int j = -fx / 2; j < fx / 2 + 1; j++) {
                        if (j + x >= 0 && j + x < width && i + y >= 0 && i + y < height) {
                            //neighbours.add(image.getRGB(x + j, y + i));
                            int color = image.getRGB(x + j, y + i);
                            rneighbours.add((color & 0xFF0000) >> 16);
                            gneighbours.add((color & 0x00FF00) >> 8);
                            bneighbours.add(color & 0x0000FF);
                        }
                    }
                }
                Collections.sort(rneighbours);
                Collections.sort(gneighbours);
                Collections.sort(bneighbours);
                int median = rneighbours.size() / 2;

                //int newColor = bneighbours.get(median) + (gneighbours.get(median) << 8) + (rneighbours.get(median) << 16);   //todo: |

                //returnImage.setRGB(x, y, neighbours.get(neighbours.size() / 2));
                returnImage.setRGB(x, y, getColorFromComponents(rneighbours.get(median), gneighbours.get(median), bneighbours.get(median)));
            }
        }
        return returnImage;
    }

    private int getColorFromComponents(int r, int g, int b)
    {
        return (r << 16) | (g << 8) | b;    //255 <<24? (альфа) todo: alpha!!
    }

    public void applySobelFilter(int threshold)
    {
        BufferedImage image = getDesaturatedImage(modifiableImagePanel.getImage());
        int height = image.getHeight();
        int width = image.getWidth();
        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double gxv = 0;
                double gyv = 0;
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        int Y;
                        if (j + x >= 0 && j + x < width && i + y >= 0 && i + y < height)
                        {
                            Y = image.getRGB(x + j, y + i) & 0x0000FF;
                        }
                        else
                        {
                            int nx = x + j, ny = y + i;
                            if(nx < 0)
                                nx = 0;
                            else if (nx >= width)
                                nx = width - 1;
                            if(ny < 0)
                                ny = 0;
                            else if (ny >= height)
                                ny = height - 1;
                            Y = image.getRGB(nx, ny) & 0x0000FF;
                        }

                        gxv += Y * sobelXMatrix[i + 1][j + 1];
                        gyv += Y * sobelYMatrix[i + 1][j + 1];
                    }
                }
                if(Math.sqrt(gxv*gxv + gyv*gyv) > threshold)
                    modifiedImagePanel.setColor(x, y, 0xFFFFFFFF);
                else
                    modifiedImagePanel.setColor(x, y, 0);

            }
        }
        modifiedImagePanel.repaint();
    }

    public void applyRobertsFilter(int threshold)
    {
        BufferedImage image = getDesaturatedImage(modifiableImagePanel.getImage());
        int height = image.getHeight();
        int width = image.getWidth();
        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int f;
                int Y = getY(image, x, y);
                if(x < width - 1 && y < height - 1)
                {
                    f = abs(Y - getY(image, x+1, y+1)) + abs(getY(image, x, y+1) - getY(image, x+1, y));
                }
                else        //считаем то что за границей нулями, т.к. продление привело бы к занулению разностей. может, тоже продлевать?? (чтобы не было белых полос снизу и справа, хотя понятна причина их поялвения)
                {
                    if(x + 1 == width && y + 1 != height)
                        f = Y + getY(image, x, y+1);
                    else if(x + 1 != width && y + 1 == height)
                        f = Y +  getY(image, x+1, y);
                    else
                        f = Y;
                }

                if(f > threshold)
                    modifiedImagePanel.setColor(x, y, 0xFFFFFFFF);
                else
                    modifiedImagePanel.setColor(x, y, 0);
            }
        }
        modifiedImagePanel.repaint();
    }

    //image should be in grayscale, so all components are equal
    private int getY(BufferedImage image, int x, int y)
    {
        return image.getRGB(x, y) & 0x0000FF;
    }

    public void applyGammaCorrection(double gamma)
    {
        BufferedImage image = modifiableImagePanel.getImage();
        for(int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = image.getRGB(x, y);
                int r = (color & 0xFF0000) >> 16;
                int g = (color & 0x00FF00) >> 8;
                int b = color & 0x0000FF;

                int nr = (int)(255*Math.pow(r/255.0, gamma));
                int ng = (int)(255*Math.pow(g/255.0, gamma));
                int nb = (int)(255*Math.pow(b/255.0, gamma));
                modifiedImagePanel.setColor(x, y, getColorFromComponents(nr, ng, nb));  //может, чуть выгоднее будет заранне взять Image и менять непосре.его?

            }
        }
        modifiedImagePanel.repaint();
    }
}

