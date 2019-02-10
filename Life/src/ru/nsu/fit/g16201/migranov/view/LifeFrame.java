package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import ru.nsu.fit.g16201.migranov.frametemplate.MainFrame;
import ru.nsu.fit.g16201.migranov.model.Field;

public class LifeFrame extends MainFrame {
    public static void main(String[] args) throws Exception
    {
        new LifeFrame();
    }

    public LifeFrame() throws Exception {
        //инициализация
        super(600, 400, "Life");  //формат, группа?


        //setSize(720, 480);

        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", "onExit");
        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About...", "Shows program version and copyright information", KeyEvent.VK_A, "About.gif", "onAbout");

        /*JToolBar toolBar = new JToolBar();
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
        setJMenuBar(menuBar);*/

        add(new FieldPanel(100, 1));

        //pack();                                                         //ужимает всё при использовании setSize
        setVisible(true);


    }

    public void onAbout()
    {
        JOptionPane.showMessageDialog(this, "group 1234", "About Init", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onExit()
    {
        System.exit(0);
    }
}
