package ru.nsu.fit.g16201.migranov;

import javax.swing.*;
import java.awt.*;

public class LifeFrame extends JFrame {
    public static void main(String[] args)
    {
        new LifeFrame();
    }

    public LifeFrame() {
        //инициализация
        super("Life");  //формат, группа?
        //setSize(720, 480);
        setPreferredSize(new Dimension(800, 600));
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        JToolBar toolBar = new JToolBar();
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem menuItem = new JMenuItem("Red");
        menu.add(menuItem);
        //menuItem.addActionListener(e -> ((MyPanel) panel).setColor(Color.RED));
        menuItem = new JMenuItem("Green");
        menu.add(menuItem);
        //menuItem.addActionListener(e -> ((MyPanel) panel).setColor(Color.GREEN));
        menuItem = new JMenuItem("Black");
        menu.add(menuItem);
        //menuItem.addActionListener(e -> ((MyPanel) panel).setColor(Color.BLACK));


        pack();                                                         //ужимает всё при использовании setSize
        setVisible(true);
    }
}
