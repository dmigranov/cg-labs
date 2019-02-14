package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.model.Field;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

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

        canvas = new BufferedImage(1366, 768, BufferedImage.TYPE_INT_ARGB); //откуда узнать размер потом?
        graphics = canvas.createGraphics();

        //TODO: listeners

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                spanFill(e.getX(), e.getY(), Color.cyan);
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        graphics.setColor(Color.BLACK);

        //g.drawImage()
        /*g.setColor(Color.BLACK);
        g.drawLine(0, 0, getWidth() - 1, getHeight() - 1); //т.к. считаем с нуля и без отиммания выходит за пределы



        g.drawRect(getWidth() * 1/4, getHeight() * 1/4, getWidth() * 1/2, getHeight() * 1/2);*/


        //в шестиугольнике радиус равен стороне

        /*for (int i = 0; i < field.getN(); i++) {

            for (int j = 0; j < field.getM(); j++) {
                int x = 50, y = 50;   //координаты середины, как-то вычисленные
                drawHexagon(graphics, x, y);
            }

        }*/


        graphics.drawOval(50, 50, 100, 100);
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


    public void spanFill(int x, int y, Color newValue)  //x и y - координаты точки, куда нажали. эта точка является зерном
    {
        //вообще, нам надо будет цвет только "инвертировать"! но радим универсальности добавлю ,пожалуй, аргуемнт цвет
        Color oldValue;


        class Span
        {
            int y;
            int lx, ly;
        }
    }
}
