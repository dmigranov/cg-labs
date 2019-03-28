package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

//TODO: легенда —это как бы отдельная функция, равномерно возрастающая по горизонтали, плюс подписи; будет красиво, если вы отобразите легенду с помощью того же кода, что и основную картинку, просто подсунув в этот код другую функцию


public class LegendPanel extends JPanel {
    private MapPanel legendMap;
    private BufferedImage canvas;
    private Graphics canvasGraphics;
    private int width, height;
    LegendPanel(int legendPanelHeight, int legendMapHeight)
    {
        this.height = legendPanelHeight;
        this.width = 1;
        setPreferredSize(new Dimension(width, legendMapHeight));
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.getGraphics();
        canvasGraphics.setColor(Color.BLACK);

        legendMap = new MapPanel();
        legendMap.setPreferredSize(new Dimension(width, legendMapHeight));
        legendMap.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(legendMap);

    }

    @Override
    public void paintComponent(Graphics g)
    {
        //todo: подписи

        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, width, height, null);
    }

    public void drawVerticalLine(int x) {
        //todo: рисовать на меньшей обалсти
        canvasGraphics.drawLine(x,0,x,height - 1);
    }

    public int getLegendWidth() {
        return width;
    }

    public int getLegendHeight() {
        return height;
    }

    public MapPanel getLegendMap() {
        return legendMap;
    }

    public void drawText(int n, double minValue, double maxValue) {
    }



}
