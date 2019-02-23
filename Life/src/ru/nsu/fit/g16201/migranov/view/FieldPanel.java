package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.model.Field;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class FieldPanel extends JPanel {
    private int k, w, r;           //w - толщина, k - длина ребра, r - радиус отрисовки
    private int xStart, yStart;
    private Field field;

    private BufferedImage canvas;
    private Graphics2D graphics;

    private BufferedImage impactCanvas;
    private Graphics2D impactGraphics;

    private static final int aliveCellColor = new Color(0x00FF09).getRGB();
    private static final int emptyCellColor = new Color(0xFFF8AF).getRGB();
    private static final int notFieldColor = new Color(0xFFFFFF).getRGB();
    private static final int borderColor = new Color(0).getRGB();

    private Map<Point, Point> centerMap = new HashMap<>();      //центр - координаты модели
    private Point current = null;       //нужно для закрашивания поля с зажатой мышкой

    private int width, heigth;

    private boolean XOR = false;
    private boolean impactsShown = false;

    public FieldPanel(int k, int w)
    {
        super();

        this.k = k;
        this.w = w;
        r = k - 1;

        xStart = 25 + k;
        yStart = 25 + k;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                current = null;
            }
            @Override
            public void mouseClicked(MouseEvent e)
            {
                //TODO: мож вынести повторяющийся код в отдельный метод? +проверить
                int x = e.getX();
                int y = e.getY();
                int currentColor = canvas.getRGB(x, y);
                if (currentColor != borderColor && currentColor != notFieldColor) {
                    Point point = getFieldCoordinates(x, y);
                    if (!XOR) {
                        spanFill(x, y, aliveCellColor);
                        field.setCell(point.y, point.x);
                    } else {
                        if (currentColor == aliveCellColor)
                            spanFill(x, y, emptyCellColor);
                        else if (currentColor == emptyCellColor)
                            spanFill(x, y, aliveCellColor);
                        field.invertCell(point.y, point.x);
                    }
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                int x = e.getX();
                int y = e.getY();
                int currentColor = canvas.getRGB(x, y);

                if (currentColor != borderColor && currentColor != notFieldColor) {

                    Point point = getFieldCoordinates(x, y);

                    if (current != null)
                    {
                        if(current.equals(point))   //если эту клетку уже обработали, то ничего делать не надо
                            return;
                    }
                    current = point;
                    if(!XOR) {
                        spanFill(x, y, aliveCellColor);
                        field.setCell(point.y, point.x);
                    }
                    else {
                        if(currentColor == aliveCellColor)
                            spanFill(x, y, emptyCellColor);
                        else if(currentColor == emptyCellColor)
                            spanFill(x, y, aliveCellColor);
                        field.invertCell(point.y, point.x);
                    }
                    repaint();
                }
            }
        });
    }

    private Point getFieldCoordinates(int x, int y) {
        int lx = x, rx = x;
        while(lx > 0 && canvas.getRGB(lx, y) != borderColor)
            lx--;
        while(rx < width - 1 && canvas.getRGB(rx, y) != borderColor)
            rx++;
        int ly = y, ry = y;
        while(ly > 0 && canvas.getRGB(x, ly) != borderColor)
            ly--;
        while(ry < heigth - 1 && canvas.getRGB(x, ry) != borderColor)
            ry++;

        int cx = (lx + rx)/2;
        int cy = (ly + ry)/2;
        if(k % 2 == 0)
        {
            cy++;
        }

        //cx 122, y 47, in map x 123 y 47

        Point point = centerMap.get(new Point(cx, cy));     //TODO: проверить корректность
        if(point != null)
            System.out.println("FOUND");
        else
            point = centerMap.get(new Point(cx+1, cy));       //необходимость позникает при w != 1 о
            if(point != null)
                System.out.println("FOUND 2");
            //todo: взять ещё другие окрестности (w = 6 k = 5) (если w > 1, то не всегда срабатывает (не всегда находит)
            else
                System.out.println("NOT FOUND");
        return point;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        System.out.println("Updated");
        g.drawImage(canvas, 0, 0, getWidth(), getHeight(), null);   //вообще, при таком построении в рисовании линий и спан не должно быть repaint(), т.к это приведёт к рекурсии
        if(impactsShown)
            g.drawImage(impactCanvas, 0, 0, getWidth(), getHeight(), null);
    }

    public void drawField()
    {
        int y = yStart; //на самом деле тоже зависит от к и w
        for (int i = 0; i < field.getN(); i++) {
            int x = xStart;
            if(i % 2 != 0)
            {
                x += (int)(Math.sqrt(3)* k /2);
            }
            for (int j = 0; j < (i % 2 == 0 ? field.getM() : field.getM() - 1); j++)
            {
                drawHexagon(graphics, x, y);
                centerMap.put(new Point(x, y), new Point(j, i));
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
            if (k % 2 == 0)
                y--;
        }

        spanFill(0, 0, notFieldColor);

        System.out.println("Drew hexagons");
    }

    private void drawHexagon(Graphics g, int x, int y) {
        //r = k
        //шестиугольник abcdeg начиная с верхнего угла
        //A, D = x, y +- k/2 (если k нечётное, а если чётное, то надо дополнительно либо верх, либо вниз сдвинуть на -1, т.к без этого рисует k + 1 точку)
        //B, C, F, G = x +- rs, y+- rh(n/p) - в зависимости от чётности k.

        int rhn = k/2;
        int rhp = (k % 2 == 0 ? k /2 - 1 : rhn);
        int kp = (k % 2 == 0 ? k - 1 : k);
        int rs =(int)(Math.sqrt(3)* k /2);

        int color = borderColor;

        if(w == 1) {
            drawLine(x, y - k, x - rs, y - rhn, color);
            drawLine(x - rs, y - rhn, x - rs, y + rhp, color);
            drawLine(x, y + kp, x - rs, y + rhp, color);

            drawLine(x, y - k, x + rs, y - rhn, color);
            drawLine(x + rs, y - rhn, x + rs, y + rhp, color);
            drawLine(x, y + kp, x + rs, y + rhp, color);
        }
        else
        {
            //setstroke
            graphics.setColor(new Color(borderColor));
            //прочекать 2 и 3 параметры
            graphics.setStroke(new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));//

            graphics.drawLine(x, y - k, x - rs, y - rhn);
            graphics.drawLine(x - rs, y - rhn, x - rs, y + rhp);
            graphics.drawLine(x, y + kp, x - rs, y + rhp);

            graphics.drawLine(x, y - k, x + rs, y - rhn);
            graphics.drawLine(x + rs, y - rhn, x + rs, y + rhp);
            graphics.drawLine(x, y + kp, x + rs, y + rhp);
        }
    }


    public void drawImpacts()
    {
        FontMetrics metrics = impactGraphics.getFontMetrics();
        int rs =(int)(Math.sqrt(3)* k /2);

        impactGraphics.clearRect(0, 0, impactCanvas.getWidth(), impactCanvas.getHeight());
        for(Map.Entry<Point, Point> entry : centerMap.entrySet())
        {
            Point centerPoint = entry.getKey();
            Point fieldPoint  = entry.getValue();
            double impact = field.getImpact(fieldPoint.y, fieldPoint.x);
            String text;
            if(impact == (int)impact)   //целое
                text = Integer.toString((int)impact);
            else
                //text = Double.toString(impact);
                text = new DecimalFormat("#.##").format(impact);
            System.out.println(metrics.stringWidth(text));
            int x = (centerPoint.x - rs) + (2 * rs + 1 - metrics.stringWidth(text)) / 2;
            impactGraphics.drawString(text, x, centerPoint.y);
            //сейчас он показывает импакты с прошлого шага, которые привели к текущей конфигурации
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

    private void drawUniversalLine(int i1, int j1, int i2, int j2, int color, boolean isInverted)
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


    private void spanFill(int x, int y, int newValue)  //x и y - координаты точки, куда нажали. эта точка является зерном
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
        rx--; //не исключаем границы
        return new Span(lx, rx, y);
    }

    public void setDrawingParameters(int w, int k) {
        this.w = w;
        this.k = k;
        r = k - 1;

        xStart = 25 + k;
        yStart = 25 + k;

        //TODO: перерисовать (+новый канвас)
    }

    public void changeImpactsShow()
    {
        impactsShown = ! impactsShown;
        if(impactsShown)
            drawImpacts();
        repaint();
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
        graphics = canvas.createGraphics();
        graphics.setColor(Color.BLACK);
        width = canvas.getWidth();
        heigth = canvas.getHeight();

        impactCanvas = new BufferedImage(1366, 768, BufferedImage.TYPE_INT_ARGB); //откуда узнать размер потом?
        impactGraphics = impactCanvas.createGraphics();
        impactGraphics.setColor(Color.BLACK);
        impactGraphics.setBackground(new Color(0,0,0,0));
        impactGraphics.setFont(impactGraphics.getFont().deriveFont(Font.BOLD, impactGraphics.getFont().getSize()));


        drawField();

        repaint();
    }

    public void setXOR(boolean state)
    {
        this.XOR = state;
    }
}
