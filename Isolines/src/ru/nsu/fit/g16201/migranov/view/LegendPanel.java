package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LegendPanel extends JPanel {
    private MapPanel legendMap;
    private BufferedImage canvas;
    private Graphics canvasGraphics;
    private int width, height;
    LegendPanel(int width, int legendPanelHeight, int legendMapHeight)
    {
        setLayout(new BorderLayout());
        this.height = legendPanelHeight;

        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.getGraphics();
        canvasGraphics.setColor(Color.BLACK);

        JPanel middlePanel = new JPanel(new BorderLayout());
        legendMap = new MapPanel(width, legendMapHeight);
        legendMap.setPreferredSize(new Dimension(width, legendMapHeight));
        legendMap.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        middlePanel.setPreferredSize(new Dimension(width, legendMapHeight));
        middlePanel.add(Box.createHorizontalStrut(20), BorderLayout.EAST);
        middlePanel.add(legendMap, BorderLayout.CENTER);
        middlePanel.add(Box.createHorizontalStrut(20), BorderLayout.WEST);
        add(middlePanel, BorderLayout.NORTH);
        revalidate();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        //todo: подписи

        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, width, height, null);
    }

    public void drawVerticalLine(int x) {
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

    public void updateSize() {
        this.width = getWidth();
        this.height = getHeight();

        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.createGraphics();

        legendMap.updateSize();
    }


}
