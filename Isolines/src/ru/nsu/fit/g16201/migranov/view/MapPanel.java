package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

public class MapPanel extends JPanel {
    private BufferedImage canvas;
    private Graphics2D canvasGraphics;
    private BufferedImage gridCanvas;
    private Graphics2D gridGraphics;
    private int width, height;

    MapPanel(int width, int height)
    {
        super();
        setDoubleBuffered(true);
        setLayout(new FlowLayout());
        this.width = 1;
        this.height = 1;
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.createGraphics();
        canvasGraphics.setColor(Color.BLACK);

        gridCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        gridGraphics = gridCanvas.createGraphics();
        gridGraphics.setColor(Color.BLACK);
        gridGraphics.setBackground(new Color(0,0,0,0));

    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, width, height, null);
        g.drawImage(gridCanvas, 0, 0, width, height, null);
    }

    public void drawLine(int x1, int y1, int x2, int y2)
    {
        canvasGraphics.drawLine(x1,y1,x2,y2);
        repaint();
    }

    public void drawGridLine(int x1, int y1, int x2, int y2)
    {
        gridGraphics.drawLine(x1,y1,x2,y2);
        repaint();
    }

    public void clearGrid()
    {
        gridGraphics.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());
    }


    public void clear() {
        canvasGraphics.setBackground(new Color(0,0,0,0));

        canvasGraphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void setColor(Color isolineColor) {
        canvasGraphics.setColor(isolineColor);
    }

    public void updateSize() {
        //todo: или лучше рисовать одну и ту же картинку на Grphics новых размеров? а то пересоздавать каждый раз..
        this.width = getWidth();
        this.height = getHeight();

        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.createGraphics();
        canvasGraphics.setColor(Color.BLACK);

        gridCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        gridGraphics = gridCanvas.createGraphics();
        gridGraphics.setColor(Color.BLACK);
        gridGraphics.setBackground(new Color(0,0,0,0));
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
        int oldValue = canvas.getRGB(x, y);
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
                canvas.setRGB(i, y, newValue);
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
            if(y < canvas.getHeight() - 1)
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
        if(canvas.getRGB(x, y) != color)
            return null;
        int lx = x, rx = x;
        while(lx > 0 && canvas.getRGB(--lx, y) == color);
        while(rx < width - 1 && canvas.getRGB(++rx, y) == color);
        lx++;
        rx--; //возвращаемся на один, т.к. зашли на границу
        return new Span(lx, rx, y);
    }
}
