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
    private int k, w, r;           //w - толщина, k - длина ребра, r - радиус отрисовки
    private Field field;

    private BufferedImage canvas;
    private Graphics graphics;

    private static final int aliveCellColor = new Color(0x00FF09).getRGB();
    private static final int emptyCellColor = new Color(0xFFF8AF).getRGB();
    private static final int notFieldColor = new Color(0xFFFFFF).getRGB();
    private static final int borderColor = new Color(0).getRGB();

    private int width, heigth;

    private boolean XOR = true;


    public FieldPanel(int k, int w)
    {
        super();

        this.k = k;
        this.w = w;
        r = k - 1;

        /*drawLine(800, 440, 820, 450, Color.BLACK.getRGB());
        drawLine(800, 450, 820, 440, Color.BLACK.getRGB());
        drawLine(800, 420, 820, 430, Color.BLACK.getRGB());
        drawLine(780, 450, 760, 440, Color.BLACK.getRGB());
        drawLine(780, 420, 760, 430, Color.BLACK.getRGB());

        drawLine(600, 420, 610, 440, Color.BLACK.getRGB());
        drawLine(580, 440, 590, 420, Color.BLACK.getRGB());
        drawLine(570, 440, 560, 420, Color.BLACK.getRGB());*/


        //TODO: listeners
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int x = e.getX();
                int y = e.getY();
                int currentColor = canvas.getRGB(x, y);
                if (currentColor != borderColor && currentColor != notFieldColor)
                {
                    if(!XOR) {
                        spanFill(x, y, aliveCellColor);
                        //TODO: поменять состояние модели, предварительно посчитав индексы поля
                        repaint();
                    }
                    else
                    {
                        //TODO: поменять состояние самой модели
                        if(currentColor == aliveCellColor)
                            spanFill(x, y, emptyCellColor);
                        else if(currentColor == emptyCellColor)
                            spanFill(x, y, aliveCellColor);
                        repaint();
                    }
                }
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        System.out.println("Updated");
        g.drawImage(canvas, 0, 0, getWidth(), getHeight(), null);   //вообще, при таком построении в рисовании линий и спан не должно быть repaint(), т.к это приведёт к рекурсии
    }

    private void drawField()
    {
        //TODO: продумать начальные; непонятки с длиной: если k = 20, то он чертит либо 21, либо 19 (если от к перейти к к-1). Это понятно почему так, но как исправить?
        int y = 50; //на самом деле тоже зависит от к и w
        for (int i = 0; i < field.getN(); i++) {
            int x = 50;
            if(i % 2 != 0)
            {
                x += (int)(Math.sqrt(3)* k /2);
            }
            for (int j = 0; j < (i % 2 == 0 ? field.getM() : field.getM() - 1); j++)
            {
                drawHexagon(graphics, x, y);
                if(field.isAlive(i, j))
                {
                    spanFill(x, y, aliveCellColor);
                }
                else
                {
                    spanFill(x, y, emptyCellColor);
                }
                x+=(int)(Math.sqrt(3) * k / 2) * 2;

            }
            y += (3 * k / 2);
        }

        spanFill(0, 0, notFieldColor);
        System.out.println("Drew hexagons");
    }

    private void drawHexagon(Graphics g, int x, int y) {
        //r = k
        //шестиугольник abcdeg начиная с левого угла
        //A, D = x +- k, y
        //B, C, F, G = x +- r/2, y+- sqrt(3)/2 * r

        int rhn = k/2;
        int rhp = k % 2 == 0 ? k /2 - 1 : rhn;
        int rs =(int)(Math.sqrt(3)* k /2);

        int color = Color.BLACK.getRGB();

        if(w == 1) {
            drawLine(x, y - k, x - rs, y - rh, color);
            drawLine(x - rs, y - rh, x - rs, y + rh, color);
            drawLine(x, y + k, x - rs, y + rh, color);

            drawLine(x, y - k, x + rs, y - rh, color);
            drawLine(x + rs, y - rh, x + rs, y + rh, color);
            System.out.println(y - rh + " " + (y+rh));
            drawLine(x, y + k, x + rs, y + rh, color);
        }
    }

    //Bresenham's line algorithm;
    //DRAWS ON CANVAS< SO SHOULD BE REPAINTED TO GRAPHICS!
    private void drawLine(int x1, int y1, int x2, int y2, int color)
    {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        //if(dy/dx > 1) //то есть если угол больше 45 в случае 1 первой четверти
        if(dy <= dx)
        {
            drawUniversalLine(x1, y1, x2, y2, color, false);
        }
        else
        {
            drawUniversalLine(y1, x1, y2, x2, color, true);
        }
    }


    public void drawUniversalLine(int i1, int j1, int i2, int j2, int color, boolean isInverted)
    {
        int err = 0;
        int di = Math.abs(i2 - i1);
        int dj = Math.abs(j2 - j1);
        
        if(i2 < i1)
        {
            int temp;
            temp = i1;
            i1 = i2;
            i2 = temp;
            temp = j1;
            j1 = j2;
            j2 = temp;
        }

        //TODO: не забыть рассчитать правильно канвас, а не то индексаутофбэнд!
        int dirj = j2 > j1 ? 1 : -1;
        for(int i = i1, j = j1; i <= i2; i++)   //границы?
        {
            err += 2 * dj;
            if(!isInverted) {
                canvas.setRGB(i, j, color);
            }
            else
                canvas.setRGB(j, i, color);
            if(err > di)
            {
                err -= 2 * di;
                j+=dirj;
            }
        }
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
        r = k - 1;

        //TODO: перерисовать (+новый канвас)
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

    public void setField(Field field)
    {
        System.out.println("New field");
        this.field = field;

        //TODO: рассчитать размер и перерисовать canvas при новых n и m, в зависимости от k и w тоже
        canvas = new BufferedImage(1366, 768, BufferedImage.TYPE_INT_ARGB); //откуда узнать размер потом?
        setPreferredSize(new Dimension(1366, 768));
        graphics = canvas.getGraphics();
        graphics.setColor(Color.BLACK);
        width = canvas.getWidth();
        heigth = canvas.getHeight();

        drawField();

        repaint();
    }
}
