package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.text.NumberFormat;

import ru.nsu.fit.g16201.migranov.controller.Controller;

import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;



//public class LifeFrame extends JFrame {
public class LifeFrame extends MainFrame {
    private FieldPanel fieldPanel;
    private Controller controller;

    private File currentFile = null;

    public static void main(String[] args) throws Exception
    {
        new LifeFrame();
    }

    private LifeFrame() throws Exception {
        //инициализация
        super(800, 600, "Untitled | Denis Migranov, 16201");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                int result = JOptionPane.showConfirmDialog(LifeFrame.this, "Do you want to save the current state of field?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(result == JOptionPane.YES_OPTION) {
                    onSave();
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
                else if(result == JOptionPane.NO_OPTION)
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });

        JMenuItem item;
        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/New", "New field", KeyEvent.VK_N, "Exit.gif", "onNew");
        addMenuItem("File/Open", "Open a field description file", KeyEvent.VK_O, "Exit.gif", "onOpen");//
        addMenuItem("File/Save", "Save a field state", KeyEvent.VK_S, "Exit.gif", "onSave");//
        item = (JMenuItem)getMenuElement("File/Save");
        item.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        addMenuItem("File/Save As", "Save a field state as", KeyEvent.VK_A, "Exit.gif", "onSaveAs");//

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
        JMenu gameMenu = (JMenu)getMenuElement("Game");
        addMenuItem("Game/Step", "Next step", KeyEvent.VK_S, "About.gif", "onStep");
        addCheckBoxMenuItem(gameMenu, "Run", "Run step-by-step execution", KeyEvent.VK_R, "About.gif", false, "onRun");

        addSubMenu("View", KeyEvent.VK_V);
        JMenu viewMenu = (JMenu)getMenuElement("View");
        addCheckBoxMenuItem(viewMenu, "Show impacts", "Indicates whether impacts should be shown", KeyEvent.VK_I, "About.gif", false, "onShowImpacts");

        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About", "Shows program version and copyright information", KeyEvent.VK_A, "About.gif", "onAbout");

        addToolBarButton("File/New");
        addToolBarButton("Game/Step");


        //todo: k = 10, w = 15: всё заливает чёрным
        //ограничить w в параметрах функцией от k!!!
        JPanel middlePanel = new JPanel();
        fieldPanel = new FieldPanel(20, 4);
        controller = new Controller(fieldPanel, this);
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
        int result = JOptionPane.showConfirmDialog(LifeFrame.this, "Do you want to save the current state of field?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(result == JOptionPane.YES_OPTION) {
            onSave();
            System.exit(0);
        }
        else if(result == JOptionPane.NO_OPTION)
            System.exit(0);

    }

    public void onOpen()
    {
        File file = getOpenFileName("txt", "A field description file");
        if(file != null) {
            currentFile = file;
            setTitle(file.getName() + " | Denis Migranov, 16201");
            controller.loadFieldFromFile(file);
        }
    }

    public void onSave()
    {
        if(currentFile != null)
            controller.saveFieldToFile(currentFile);
        else
            onSaveAs();
    }

    public void onSaveAs()
    {
        //todo: индикатор изменений в fieldpanel, чтобы если ничего не было изменено, не пересправшивать
        File file = getSaveFileName("txt", "A field description file");
        if(file != null) {
            currentFile = file;
            setTitle(file.getName() + " | Denis Migranov, 16201");
            controller.saveFieldToFile(file);
        }
    }

    public void onNew()
    {
        int result = JOptionPane.showConfirmDialog(LifeFrame.this, "Do you want to save the current state of field?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(result == JOptionPane.CANCEL_OPTION)
            return;
        if(result == JOptionPane.YES_OPTION) {
            onSave();
        }

        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter numberFormatter = new NumberFormatter(format);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setMinimum(0);

        JFormattedTextField mField = new JFormattedTextField(numberFormatter);
        JFormattedTextField nField = new JFormattedTextField(numberFormatter);

        //todo: ограничить воод
        JPanel mnPanel = new JPanel();
        mnPanel.add(new JLabel("m: "));
        mnPanel.add(mField);
        mnPanel.add(Box.createHorizontalStrut(10));
        mnPanel.add(new JLabel("n: "));
        mnPanel.add(nField);

        if(JOptionPane.showConfirmDialog(this, mnPanel, "Field parameters", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {

        }
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

    public void onRun()
    {
        if(controller.isRunning())
            controller.setRunning(false);
        else
        {
            controller.run();
        }
    }

    private void addRadioButtonMenuItem(JMenu parent, String title, String tooltip, int mnemonic, String icon, ButtonGroup group, boolean state, String actionMethod) throws SecurityException, NoSuchMethodException
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

    private void addCheckBoxMenuItem(JMenu parent, String title, String tooltip, int mnemonic, String icon, boolean state, String actionMethod) throws SecurityException, NoSuchMethodException
    {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(title, state);//icons description etc
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

    public void setActive(boolean isActive)
    {

    }
}
