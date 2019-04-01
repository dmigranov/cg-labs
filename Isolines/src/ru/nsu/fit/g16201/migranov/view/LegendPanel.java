package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class LegendPanel extends JPanel {
    private MapPanel legendMap;
    private BufferedImage canvas;
    private Graphics2D canvasGraphics;
    private int width, height;
    private int offset = 20;
    LegendPanel(int width, int legendPanelHeight, int legendMapHeight)
    {
        setLayout(new BorderLayout());
        this.height = legendPanelHeight;

        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.createGraphics();
        canvasGraphics.setColor(Color.BLACK);

        JPanel middlePanel = new JPanel(new BorderLayout());
        legendMap = new MapPanel(width, legendMapHeight);
        legendMap.setPreferredSize(new Dimension(width, legendMapHeight));
        legendMap.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        middlePanel.setPreferredSize(new Dimension(width, legendMapHeight));
        middlePanel.add(Box.createHorizontalStrut(offset), BorderLayout.EAST);
        middlePanel.add(legendMap, BorderLayout.CENTER);
        middlePanel.add(Box.createHorizontalStrut(offset), BorderLayout.WEST);
        add(middlePanel, BorderLayout.SOUTH);
        revalidate();
    }

    @Override
    public void paintComponent(Graphics g)
    {
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

    public void drawText(int n, double minValue, double maxValue, Model legendModel) {
        canvasGraphics.setBackground(new Color(0,0,0,0));
        canvasGraphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        FontMetrics metrics = canvasGraphics.getFontMetrics();
        for (int l = 1; l <= n; l++) {
            double z = minValue + l * (maxValue - minValue) / (n + 1);

            int centerX = offset + (int) (legendMap.getWidth() * legendModel.getValue(l, 0));

            String text;
            if (z == (int) z)   //целое
                text = Integer.toString((int) z);
            else
                text = new DecimalFormat("#.##").format(z);
            int x = (centerX - 20) + (2 * 20 + 1 - metrics.stringWidth(text)) / 2;
            int y = (height - legendMap.getHeight()) - 5;
            canvasGraphics.drawString(text, x, y);
        }
    }

    public void updateSize() {
        this.width = getWidth();
        this.height = getHeight();

        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.createGraphics();
        canvasGraphics.setColor(Color.BLACK);

        legendMap.updateSize();
    }


}
