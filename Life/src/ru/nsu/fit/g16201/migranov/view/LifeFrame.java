package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;

import ru.nsu.fit.g16201.migranov.controller.Controller;
import ru.nsu.fit.g16201.migranov.model.Field;
import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;

//public class LifeFrame extends JFrame {
public class LifeFrame extends MainFrame {
    private Field field;
    private FieldPanel fieldPanel;
    private Controller controller;

    public static void main(String[] args) throws Exception
    {
        new LifeFrame();
    }

    private LifeFrame() throws Exception {
        //инициализация
        super(720, 480, "Life | Denis Migranov, 16201");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fieldPanel = new FieldPanel(30, 1);
        controller = new Controller(fieldPanel);

        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", "onExit");
        addMenuItem("File/Open", "Open a field description file", KeyEvent.VK_O, "Exit.gif", "onOpen");//

        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About...", "Shows program version and copyright information", KeyEvent.VK_A, "About.gif", "onAbout");

        JPanel middlePanel = new JPanel();  //TODO: можно ли обойтись без лишней панели? и сделать так, чтобы всё было красиво (слева ввреху приклеено?)
        middlePanel.add(fieldPanel);
        JScrollPane scrollPane = new JScrollPane(middlePanel);
        scrollPane.setWheelScrollingEnabled(true);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
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
