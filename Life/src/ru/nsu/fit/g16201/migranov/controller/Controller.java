package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Field;
import ru.nsu.fit.g16201.migranov.view.FieldPanel;
import ru.nsu.fit.g16201.migranov.view.LifeFrame;

import java.awt.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;

public class Controller {
    private static final String title = "Migranov, 16201";
    private FieldPanel fieldPanel;
    private Field field;
    private JFrame lifeFrame;
    private int period = 1000;
    private boolean isRunning = false;

    private File currentFile = null;

    //создать интерфейс типа fieldPanel а то как-то не по ооп
    public Controller(FieldPanel fieldPanel, LifeFrame lifeFrame) {
        this.fieldPanel = fieldPanel;
        this.lifeFrame = lifeFrame;
        field = new Field(20, 20);
        String fileName = "Untitled";
        lifeFrame.setTitle(fileName + " | " + title);
        fieldPanel.setField(field);
    }

    public void loadFieldFromFile(File file)
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            line = br.readLine();
            line = line.substring(0, line.indexOf('/'));
            String[] substrings = line.split(" ");
            int m = Integer.parseInt(substrings[0]);
            int n = Integer.parseInt(substrings[1]);
            field = new Field(m, n);

            line = br.readLine();
            line = line.substring(0, line.indexOf('/'));
            int w = Integer.parseInt(line);
            line = br.readLine();
            line = line.substring(0, line.indexOf('/'));
            int k = Integer.parseInt(line);
            fieldPanel.setDrawingParameters(w, k);

            line = br.readLine();
            line = line.substring(0, line.indexOf('/'));
            int all = Integer.parseInt(line);

            //в файле клетки в формате xy, а у меня во внутреннем представлении - yx!
            while ((line = br.readLine()) != null)
            {
                substrings = line.split(" ");
                int x = Integer.parseInt(substrings[0]);
                int y = Integer.parseInt(substrings[1]);
                field.setCell(y, x);
                all--;
            }
            //TODO: проверить all и выкинуть exception если что-то не так
            System.out.println("All: " + all);
            if(all > 0)     //прочли меньше чем обещано; А ЕСЛИ БОЛЬШЕ?
                throw new IOException();

            fieldPanel.setField(field);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(lifeFrame, "Could not read this file", "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    public void step()
    {
        field.step();
        fieldPanel.drawField();
        fieldPanel.drawImpacts();
        fieldPanel.repaint();
    }


    public void clearField() {
        field.clear();
        fieldPanel.drawField();
        fieldPanel.drawImpacts();
        fieldPanel.repaint();
    }

    public void run() {
        setRunning(true);
        final Timer t = new Timer(period, null);
        t.addActionListener( e -> {
            if(!isRunning) {
                t.stop();
                fieldPanel.setActive(true);
                return;
            }
            step();

        });
        fieldPanel.setActive(false);
        t.start();
        //TODO: отключить возможноость трогать поле!
    }

    public void saveFieldToFile(File file) {
        try
        {
            PrintWriter pw = new PrintWriter(file);
            pw.println(field.getM() + " " + field.getN());
            pw.println(fieldPanel.getW());
            pw.println(fieldPanel.getK());

            List<Point> notNullCells = new LinkedList<>();
            for(int y = 0; y < field.getN(); y++) {
                for (int x = 0; x < (y % 2 == 0 ? field.getM() : field.getM() - 1); x++) {
                    if(field.isAlive(y, x))
                        notNullCells.add(new Point(x, y));
                }
            }

            pw.println(notNullCells.size());
            for(Point p : notNullCells)
            {
                pw.println(p.x + " " + p.y);
            }

            pw.close();
        }
        catch(IOException e)
        {
            //JOptionPane.showMessageDialog(lifeFrame, "Could not read this file", "Error", JOptionPane.ERROR);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning)
    {
        this.isRunning = isRunning;
    }
}
