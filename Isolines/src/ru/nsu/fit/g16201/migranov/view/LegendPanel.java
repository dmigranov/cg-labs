package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

public class LegendPanel extends JPanel {
    private BufferedImage canvas;
    private Graphics canvasGraphics;
    private int width, height;
    LegendPanel(int width, int height)
    {
        this.width = width;
        this.height = height;
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.getGraphics();
        canvasGraphics.setColor(Color.BLACK);
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
                    //i += (newSpan.rx - newSpan.lx);   //это неправильно, смотри в тетрадке на первой. исправил чтобы учитывать этот случай
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
