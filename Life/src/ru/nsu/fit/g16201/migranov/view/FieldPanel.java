package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.model.Field;

import javax.swing.*;
import java.awt.*;

public class FieldPanel extends JPanel {
    private int k, w;           //w - толщина, k - длина ребра
    private Field field;

    public FieldPanel(int k, int w)
    {
        super();
        this.k = k;
        this.w = w;
        field = new Field(5,5);     //TODO: откуда передавать?

        field.step();

        //TODO: listeners
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);


        /*g.setColor(Color.BLACK);
        g.drawLine(0, 0, getWidth() - 1, getHeight() - 1); //т.к. считаем с нуля и без отиммания выходит за пределы



        g.drawRect(getWidth() * 1/4, getHeight() * 1/4, getWidth() * 1/2, getHeight() * 1/2);*/


        //в шестиугольнике радиус равен стороне

        for (int i = 0; i < field.getN(); i++)
        {

            for (int j = 0; j < field.getM(); j++)
            {
                int x = 50, y = 50;   //координаты середины, как-то вычисленные
                drawHexagon(g, x, y);
            }

        }
    }

    private void drawHexagon(Graphics g, int x, int y) {
        //r = k
        //шестиугольник abcdeg начиная с левого угла
        //A, D = x +- k, y
        //B, C, F, G = x +- r/2, y+- sqrt(3)/2 * r

        int rh = k/2;
        int rs =(int)Math.sqrt(3)* k /2;

        g.drawLine(x - k, y, x - rh, y + rs);
        g.drawLine(x - rh, y + rs, x + rh, y + rs);
        g.drawLine(x + k, y, x + rh, y + rs);

        g.drawLine(x - k, y, x - rh, y - rs);
        g.drawLine(x - rh, y - rs, x + rh, y - rs);
        g.drawLine(x + k, y, x + rh, y - rs);
    }
}
