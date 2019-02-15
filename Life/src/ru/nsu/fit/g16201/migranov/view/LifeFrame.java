package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;

import ru.nsu.fit.g16201.migranov.controller.Controller;
import ru.nsu.fit.g16201.migranov.model.Field;
import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;

//public class LifeFrame extends JFrame {
public class LifeFrame extends MainFrame {
    Field field;
    FieldPanel fieldPanel;
    Controller controller;

    public static void main(String[] args) throws Exception
    {
        new LifeFrame();
    }

    private LifeFrame() throws Exception {
        //инициализация
        //super(600, 400, "Life");  //формат, группа?
        super();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //setSize(720, 480);
        fieldPanel = new FieldPanel(30, 1);

        controller = new Controller(fieldPanel);

        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", "onExit");
        addMenuItem("File/Open", "Open a field file", KeyEvent.VK_O, "Exit.gif", "onOpen");//

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

        JPanel middlePanel = new JPanel();  //можно ли обойтись без лишней панели?
        middlePanel.add(fieldPanel);
        JScrollPane scrollPane = new JScrollPane(middlePanel);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setContentPane(scrollPane);

        pack();                                                         //ужимает всё при использовании setSize
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

    public void onOpen()
    {
        File file = getOpenFileName("txt", "A field description file");
        controller.loadFieldFromFile(file);

    }
}
