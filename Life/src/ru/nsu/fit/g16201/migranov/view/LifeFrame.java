package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.controller.OpenFileActionListener;

import javax.swing.*;

public class LifeFrame extends JFrame {
    public static void main(String[] args) throws Exception
    {
        new LifeFrame();
    }

    private LifeFrame()
    {
        super();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);



        JPanel middlePanel = new JPanel();  //можно ли обойтись без лишней панели?
        middlePanel.add(new FieldPanel(30, 1));
        JScrollPane scrollPane = new JScrollPane(middlePanel);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setContentPane(scrollPane);


        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        //TODO: подписи, картинки...
        JMenuItem menuItem;
        menuItem = new JMenuItem("Open");
        menu.add(menuItem);
        menuItem.addActionListener(new OpenFileActionListener());
        menuItem = new JMenuItem("Exit");
        menu.add(menuItem);


        setJMenuBar(menuBar);


        pack();             //ужимает всё при использовании setSize
        setVisible(true);
    }
}
