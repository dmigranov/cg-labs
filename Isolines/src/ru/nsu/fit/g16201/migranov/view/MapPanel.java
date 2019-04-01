package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

public class MapPanel extends JPanel {
    private BufferedImage lineCanvas;
    private Graphics2D lineGraphics;

    private BufferedImage userLineCanvas;
    private Graphics2D userLineGraphics;

    private BufferedImage gridCanvas;
    private Graphics2D gridGraphics;

    private BufferedImage gridPointsCanvas;
    private Graphics2D gridPointsGraphics;

    private BufferedImage colorCanvas;
    private Graphics2D colorGraphics;

    private BufferedImage interpolatedCanvas;
    private Graphics2D interpolatedGraphics;

    //todo: когда буду реализовывать дин. рисование изолиний, делать это на отдельном канвасе (СКОРОСТЬ!)

    private int width, height;
    private Color isolineColor;
    private int isolineRGB;
    private boolean areGridPointsEnabled = false;
    private boolean interpolationEnabled = false;
    private boolean isolinesEnabled = true;

    static private final Color gridColor = Color.BLACK;

    MapPanel(int width, int height)
    {
        super();
        setDoubleBuffered(true);
        setLayout(new FlowLayout());
        this.width = 1;
        this.height = 1;
        lineCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        lineGraphics = lineCanvas.createGraphics();

        userLineCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        userLineGraphics = lineCanvas.createGraphics();

        colorCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        colorGraphics = colorCanvas.createGraphics();

        interpolatedCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        interpolatedGraphics = interpolatedCanvas.createGraphics();

        gridCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        gridGraphics = gridCanvas.createGraphics();
        gridGraphics.setColor(gridColor);
        gridGraphics.setBackground(new Color(0,0,0,0));

        gridPointsCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        gridPointsGraphics = gridPointsCanvas.createGraphics();
        gridPointsGraphics.setColor(gridColor);
        gridPointsGraphics.setBackground(new Color(0,0,0,0));

    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(!interpolationEnabled)
            g.drawImage(colorCanvas, 0, 0, width, height, null);
        else
            g.drawImage(interpolatedCanvas, 0, 0, width, height, null);
        if(isolinesEnabled)
            g.drawImage(lineCanvas, 0, 0, width, height, null);
        g.drawImage(userLineCanvas, 0, 0, width, height, null);
        g.drawImage(gridCanvas, 0, 0, width, height, null);
        if(areGridPointsEnabled)
            g.drawImage(gridPointsCanvas, 0, 0, width, height, null);

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
        colorGraphics.clearRect(0, 0, colorCanvas.getWidth(), colorCanvas.getHeight());

    }

    public void setColor(Color isolineColor) {
        this.isolineColor = isolineColor;
        isolineRGB = isolineColor.getRGB();
        lineGraphics.setColor(isolineColor);
        userLineGraphics.setColor(isolineColor);
    }

    public void updateSize() {
        this.width = getWidth();
        this.height = getHeight();

        lineCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        lineGraphics = lineCanvas.createGraphics();
        lineGraphics.setColor(isolineColor);

        userLineCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        userLineGraphics = lineCanvas.createGraphics();
        userLineGraphics.setColor(isolineColor);

        colorCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        colorGraphics = colorCanvas.createGraphics();

        interpolatedCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        interpolatedGraphics = interpolatedCanvas.createGraphics();

        gridCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        gridGraphics = gridCanvas.createGraphics();
        gridGraphics.setColor(Color.BLACK);
        gridGraphics.setBackground(new Color(0,0,0,0));

        gridPointsCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        gridPointsGraphics = gridPointsCanvas.createGraphics();
        gridPointsGraphics.setColor(gridColor);
        gridPointsGraphics.setBackground(new Color(0,0,0,0));
    }

    public void drawGridRect(int u1, int v1, int u2, int v2) {
        gridGraphics.drawRect(u1, v1, u2 - u1, v2 - v1);
    }

    public void drawGridPoint(int u, int v) {
        gridPointsGraphics.fillOval(u-4, v-4, 8, 8);
    }

    public void drawGridPoint(int u, int v, int color) {
        gridPointsGraphics.setColor(new Color(color));
        gridPointsGraphics.fillOval(u-4, v-4, 8, 8);
    }


    public void clearGridPoints() {
        gridPointsGraphics.clearRect(0, 0, gridPointsCanvas.getWidth(), gridPointsCanvas.getHeight());

    }

    public void setGridPointsEnabled(boolean gridPointsEnabled) {
        areGridPointsEnabled = gridPointsEnabled;
    }

    public void drawUserLine(int u1, int v1, int u2, int v2) {
        userLineGraphics.drawLine(u1,v1,u2,v2);
    }

    public void clearUserLine() {
    }

    public void setInterpolationEnabled(boolean interpolationEnabled) {
        this.interpolationEnabled = interpolationEnabled;
    }

    public int getRGB(int x, int y) {
        //try {
            return colorCanvas.getRGB(x, y);
        /*}
        catch (ArrayIndexOutOfBoundsException e)
        {

        }
        return -1;-*/
    }

    public void setIsolinesEnabled(boolean isolinesEnabled) {
        this.isolinesEnabled = isolinesEnabled;
    }

    public void drawColorLine(int u1, int v1, int u2, int v2, Color color) {
        colorGraphics.setColor(color);
        colorGraphics.drawLine(u1,v1,u2,v2);
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
        if(lx == rx) {
            return null;
        }
        lx++;
        rx--; //возвращаемся на один, т.к. зашли на границу
        return new Span(lx, rx, y);
    }

    public void paintPixelInterpolated(int x, int y, int color)
    {
        //interpolatedCanvas.setRGB(x, y, color);   //пчему не работает???
        interpolatedGraphics.setColor(new Color(color));
        interpolatedGraphics.drawLine(x,y,x,y);
    }

    public void drawLineInterpolated(int x1, int y1, int x2, int y2, int color)
    {
        //interpolatedCanvas.setRGB(x, y, color);   //пчему не работает???
        interpolatedGraphics.setColor(new Color(color));
        interpolatedGraphics.drawLine(x1,y1,x2,y2);
    }

    public void paintPixel(int x, int y, int color)
    {
        colorCanvas.setRGB(x, y, color);   //пчему не работает???
        /*interpolatedGraphics.setColor(new Color(color));
        interpolatedGraphics.drawLine(x,y,x,y);*/
    }
}
