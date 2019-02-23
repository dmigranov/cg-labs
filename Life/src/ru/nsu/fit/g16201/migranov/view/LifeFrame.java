package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;

import ru.nsu.fit.g16201.migranov.controller.Controller;

import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;

import static java.awt.event.KeyEvent.VK_R;

//public class LifeFrame extends JFrame {
public class LifeFrame extends MainFrame {
    private FieldPanel fieldPanel;
    private Controller controller;

    public static void main(String[] args) throws Exception
    {
        new LifeFrame();
    }

    private LifeFrame() throws Exception {
        //инициализация
        super(800, 600, "Life | Denis Migranov, 16201");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/New", "New field", KeyEvent.VK_N, "Exit.gif", "onNew");
        addMenuItem("File/Open", "Open a field description file", KeyEvent.VK_O, "Exit.gif", "onOpen");//
        addMenuItem("File/Save As", "Save a field state", KeyEvent.VK_S, "Exit.gif", "onSaveAs");//

        addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", "onExit");

        addSubMenu("Edit", KeyEvent.VK_E);
        JMenu editMenu = (JMenu)getMenuElement("Edit");
        ButtonGroup group = new ButtonGroup();
        addRadioButtonMenuItem(editMenu, "Replace", "Replace mode", KeyEvent.VK_R, "About.gif", group, true, "onReplace");
        addRadioButtonMenuItem(editMenu,"XOR", "XOR mode", KeyEvent.VK_X, "XOR.gif", group, false, "onXOR");
        addToolBarToggleButton("Edit/Replace");
        addToolBarToggleButton("Edit/XOR");
        addMenuSeparator("Edit");
        addMenuItem("Edit/Clear", "Clear the field", KeyEvent.VK_C, "Exit.gif", "onClear");


        addSubMenu("Game", KeyEvent.VK_G);
        addMenuItem("Game/Step", "Next step", KeyEvent.VK_S, "About.gif", "onStep");

        addSubMenu("View", KeyEvent.VK_V);
        addMenuItem("View/Show impacts", "Shows impacts of cells", KeyEvent.VK_I, "About.gif", "onShowImpacts");
//todo: ticked impact!

        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About", "Shows program version and copyright information", KeyEvent.VK_A, "About.gif", "onAbout");

        addToolBarButton("File/New");
        addToolBarButton("Game/Step");


        //todo: k = 10, w = 15: всё заливает чёрным
        //ограничить w в параметрах функцией от k!!!
        JPanel middlePanel = new JPanel();
        fieldPanel = new FieldPanel(20, 4, middlePanel);
        controller = new Controller(fieldPanel);
        middlePanel.add(fieldPanel);
        middlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JScrollPane scrollPane = new JScrollPane(middlePanel);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);

        //TODO: статусбар


        setMinimumSize(new Dimension(800, 600));
        setVisible(true);


    }

    public void onAbout()
    {
        JOptionPane.showMessageDialog(this, "group 16201", "About Init", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onExit()
    {
        System.exit(0);
    }

    public void onOpen()
    {
        File file = getOpenFileName("txt", "A field description file");
        if(file != null)
            controller.loadFieldFromFile(file);

    }

    public void onNew()
    {
        //спросить параметры
    }

    public void onSaveAs()
    {

    }

    public void onStep()
    {
        controller.step();
    }

    public void onReplace()
    {
        fieldPanel.setXOR(false);
    }

    public void onXOR()
    {
        fieldPanel.setXOR(true);
    }

    public void onShowImpacts()
    {
        fieldPanel.changeImpactsShow();
    }

    public void onClear()
    {
        controller.clearField();
    }

    public void addRadioButtonMenuItem(JMenu parent, String title, String tooltip, int mnemonic, String icon, ButtonGroup group, boolean state, String actionMethod) throws SecurityException, NoSuchMethodException
    {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(title, state);//icons description etc
        if(icon != null)
            item.setIcon(new ImageIcon(getClass().getResource("resources/"+icon), title));

        item.setMnemonic(mnemonic);
        item.setToolTipText(tooltip);

        final Method method = getClass().getMethod(actionMethod);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    method.invoke(LifeFrame.this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        parent.add(item);
        group.add(item);
    }

    public JToggleButton createToolBarToggleButton(JMenuItem item)
    {
        JToggleButton button = new JToggleButton(item.getIcon());
        /*for(ActionListener listener: item.getActionListeners())
            button.addActionListener(listener);*/
        button.setToolTipText(item.getToolTipText());
        button.setModel(item.getModel());   //button state model
        return button;
    }

    public JToggleButton createToolBarToggleButton(String menuPath)
    {
        JMenuItem item = (JMenuItem)getMenuElement(menuPath);
        if(item == null)
            throw new InvalidParameterException("Menu path not found: "+menuPath);
        return createToolBarToggleButton(item);
    }

    public void addToolBarToggleButton(String menuPath)
    {
        toolBar.add(createToolBarToggleButton(menuPath));
    }
}
