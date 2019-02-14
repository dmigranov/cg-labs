package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.model.Field;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

public class FieldPanel extends JPanel {
    private int k, w;           //w - толщина, k - длина ребра
    private Field field;

    private BufferedImage canvas;
    private Graphics2D graphics;


    public FieldPanel(int k, int w)
    {
        super();
        this.k = k;
        this.w = w;
        field = new Field(5,5);     //TODO: откуда передавать?

        field.step();

        //TODO: размер канваса?
        canvas = new BufferedImage(1366, 768, BufferedImage.TYPE_INT_ARGB); //откуда узнать размер потом?
        graphics = canvas.createGraphics();

        //TODO: listeners

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                spanFill(e.getX(), e.getY(), Color.cyan.getRGB());
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        graphics.setColor(Color.BLACK);

        //g.drawImage()
        /*g.setColor(Color.BLACK);
        g.drawLine(0, 0, getWidth() - 1, getHeight() - 1); //т.к. считаем с нуля и без отиммания выходит за пределы



        g.drawRect(getWidth() * 1/4, getHeight() * 1/4, getWidth() * 1/2, getHeight() * 1/2);*/


        //в шестиугольнике радиус равен стороне

        for (int i = 0; i < field.getN(); i++) {

            for (int j = 0; j < field.getM(); j++) {
                int x = 50, y = 50;   //координаты середины, как-то вычисленные
                drawHexagon(graphics, x, y);
            }

        }


        g.drawImage(canvas, 0, 0, getWidth(), getHeight(), null);
    }

    private void drawHexagon(Graphics g, int x, int y) {
        //r = k
        //шестиугольник abcdeg начиная с левого угла
        //A, D = x +- k, y
        //B, C, F, G = x +- r/2, y+- sqrt(3)/2 * r

        //TODO: это рисует повернутый на 90 градусов гексагон! исправить в брезенхэме
        int rh = k/2;
        int rs =(int)(Math.sqrt(3)* k /2);

        g.drawLine(x - k, y, x - rh, y + rs);
        g.drawLine(x - rh, y + rs, x + rh, y + rs);
        g.drawLine(x + k, y, x + rh, y + rs);

        g.drawLine(x - k, y, x - rh, y - rs);
        g.drawLine(x - rh, y - rs, x + rh, y - rs);
        g.drawLine(x + k, y, x + rh, y - rs);
    }

    //Bresenham's line algorithm
    private void drawLine(int x1)
    {

    }


    public void spanFill(int x, int y, int newValue)  //x и y - координаты точки, куда нажали. эта точка является зерном
    {
        //вообще, нам надо будет цвет только "инвертировать"! но радим универсальности добавлю ,пожалуй, аргуемнт цвет
        int oldValue = canvas.getRGB(x, y);


        class Span
        {
            int y;
            int lx, rx;

            Span(int x, int y, int color)
            {
                this.y = y;
                int lx = x, rx = x;
                while(lx > 0 && canvas.getRGB(--lx, y) == color);
                while(rx < canvas.getWidth() - 1 && canvas.getRGB(++rx, y) == color);
                this.lx = lx++;
                this.rx = rx--; //не исключаем границы
            }

        }

        Deque<Span> spanStack = new ArrayDeque<>();
        Span span = new Span(x, y, oldValue);
        spanStack.push(span);

        while (!spanStack.isEmpty())
        {
            span = spanStack.pop();
            for(int i = span.lx; i <= span.rx; i++)
                canvas.setRGB(i, y, newValue);
        }



        repaint();
    }



}
