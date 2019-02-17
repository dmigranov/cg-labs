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
    private Graphics graphics;

    private int width, heigth;


    public FieldPanel(int k, int w)
    {
        super();

        this.k = k;
        this.w = w;

        // TODO: надо пересоздавать canvas при новых n и m, в зависимости от k и w тоже
        canvas = new BufferedImage(1366, 768, BufferedImage.TYPE_INT_ARGB); //откуда узнать размер потом?
        setPreferredSize(new Dimension(1366, 768));

        graphics = canvas.getGraphics();
        graphics.setColor(Color.BLACK);

        width = canvas.getWidth();
        heigth = canvas.getHeight();

        //drawLine(800, 440, 820, 450, Color.BLACK.getRGB());
        drawLine(800, 450, 820, 440, Color.BLACK.getRGB());
        graphics.drawLine(800, 420, 820, 430);

        //TODO: listeners
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int x = e.getX();
                int y = e.getY();
                if (canvas.getRGB(x, y) != Color.BLACK.getRGB())
                    spanFill(x, y, Color.RED.getRGB());
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        //в шестиугольнике радиус равен стороне; ЭТО ПОТОМ УДАЛИТЬ!!!
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



        //g.drawLine(800, 430, 820, 420);




    }

    //Bresenham's line algorithm
    private void drawLine(int x1, int y1, int x2, int y2, int color)
    {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int err = 0;

        int diry = y2 > y1 ? 1 : -1;
        //todo: учесть 8 направлений!!!

        int y = y1;
        for(int x = x1; x <= x2; x++)   //границы?
        {
            err += 2 * dy;
            canvas.setRGB(x, y, color);
            System.out.println(x + " " + y);
            if(err > dx)
            {
                err -= 2 * dx;
                y+=diry;
            }
        }

        repaint();
    }


    public void spanFill(int x, int y, int newValue)  //x и y - координаты точки, куда нажали. эта точка является зерном
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
                    i += (newSpan.rx - newSpan.lx);
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
                    i += (newSpan.rx - newSpan.lx);
                    spanStack.push(newSpan);
                }
            }
        }
        repaint();
    }

    private Span getSpan(int x, int y, int color)
    {
        if(canvas.getRGB(x, y) != color)
            return null;
        int lx = x, rx = x;
        while(lx > 0 && canvas.getRGB(--lx, y) == color);
        while(rx < width - 1 && canvas.getRGB(++rx, y) == color);
        lx++;
        rx--; //не исключаем границы
        return new Span(lx, rx, y);
    }

    public void setDrawingParameters(int w, int k) {
        this.w = w;
        this.k = k;
        //TODO: перерисовать
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

    public Field getField() {
        return field;
    }

    public void setField(Field field)
    {
        //TODO: новый пустой канвас и панель нужных размеров + перерисовать
        System.out.println("New field");
        this.field = field;

        canvas = new BufferedImage(3000, 2000, BufferedImage.TYPE_INT_ARGB); //откуда узнать размер потом?
        setPreferredSize(new Dimension(3000, 2000));

        graphics = canvas.getGraphics();
        graphics.setColor(Color.BLACK);
    }
}
