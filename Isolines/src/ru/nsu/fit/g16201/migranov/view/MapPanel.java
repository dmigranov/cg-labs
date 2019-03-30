package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

public class MapPanel extends JPanel {
    private BufferedImage lineCanvas;
    private Graphics2D lineGraphics;

    private BufferedImage gridCanvas;
    private Graphics2D gridGraphics;

    private BufferedImage colorCanvas;
    private Graphics2D colorGraphics;

    private int width, height;
    private Color isolineColor;
    private int isolineRGB;

    MapPanel(int width, int height)
    {
        super();
        setDoubleBuffered(true);
        setLayout(new FlowLayout());
        this.width = 1;
        this.height = 1;
        lineCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        lineGraphics = lineCanvas.createGraphics();

        colorCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        colorGraphics = colorCanvas.createGraphics();

        gridCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        gridGraphics = gridCanvas.createGraphics();
        gridGraphics.setColor(Color.BLACK);
        gridGraphics.setBackground(new Color(0,0,0,0));

    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(colorCanvas, 0, 0, width, height, null);
        g.drawImage(lineCanvas, 0, 0, width, height, null);
        g.drawImage(gridCanvas, 0, 0, width, height, null);
    }

    public void drawLine(int x1, int y1, int x2, int y2)
    {
        lineGraphics.drawLine(x1,y1,x2,y2);
    }

    public void drawGridLine(int x1, int y1, int x2, int y2)
    {
        gridGraphics.drawLine(x1,y1,x2,y2);
    }

    public void clearGrid()
    {
        gridGraphics.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());
    }


    public void clear() {
        lineGraphics.setBackground(new Color(0,0,0,0));

        lineGraphics.clearRect(0, 0, lineCanvas.getWidth(), lineCanvas.getHeight());
    }

    public void setColor(Color isolineColor) {
        this.isolineColor = isolineColor;
        isolineRGB = isolineColor.getRGB();
        lineGraphics.setColor(isolineColor);
    }

    public void updateSize() {
        this.width = getWidth();
        this.height = getHeight();

        lineCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        lineGraphics = lineCanvas.createGraphics();
        lineGraphics.setColor(isolineColor);

        colorCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        colorGraphics = colorCanvas.createGraphics();

        gridCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        gridGraphics = gridCanvas.createGraphics();
        gridGraphics.setColor(Color.BLACK);
        gridGraphics.setBackground(new Color(0,0,0,0));
    }

    public void drawGridRect(int u1, int v1, int u2, int v2) {
        gridGraphics.drawRect(u1, v1, u2 - u1, v2 - v1);
    }


    class Span
    {
        int y;
        int lx, rx;

        Span(int lx, int rx, int y)
        {
            this.y = y;
            this.lx = lx;
            this.rx = rx;
        }
    }

    public void spanFill(int x, int y, int newValue)
    {
        int oldValue = colorCanvas.getRGB(x, y);
        if(oldValue == newValue)
            return;

        Deque<Span> spanStack = new ArrayDeque<>();
        Span span = getSpan(x, y, oldValue);
        if (span != null)
            spanStack.push(span);

        while (!spanStack.isEmpty())
        {
            span = spanStack.pop();
            y = span.y;
            for(int i = span.lx; i <= span.rx; i++) {
                colorCanvas.setRGB(i, y, newValue);
            }
            if(y > 0)
            {
                for (int i = span.lx; i <= span.rx; i+=2)    //прибавляю сразу два, чтобы не проверять заведомый пробел между двумя спанами
                {
                    Span newSpan = getSpan(i, y - 1, oldValue);
                    if (newSpan == null)
                        continue;
                    i += (newSpan.rx - (newSpan.lx > span.lx ? newSpan.lx : span.lx));
                    spanStack.push(newSpan);
                }
            }
            if(y < colorCanvas.getHeight() - 1)
            {
                for(int i = span.lx; i <= span.rx; i+=2)
                {
                    Span newSpan = getSpan(i, y+1, oldValue);
                    if (newSpan == null)
                        continue;
                    i += (newSpan.rx - (newSpan.lx > span.lx ? newSpan.lx : span.lx));
                    spanStack.push(newSpan);
                }
            }
        }
    }

    private Span getSpan(int x, int y, int color)
    {
        if(colorCanvas.getRGB(x, y) != color)
            return null;
        int lx = x, rx = x;
        while(lx > 0 && colorCanvas.getRGB(lx, y) == color && lineCanvas.getRGB(lx, y) != isolineRGB) --lx;
        while(rx < width - 1 && colorCanvas.getRGB(rx, y) == color && lineCanvas.getRGB(rx, y) != isolineRGB) ++rx;
        if(lx == rx)
            return null;
        lx++;
        rx--; //возвращаемся на один, т.к. зашли на границу
        return new Span(lx, rx, y);
    }
}
