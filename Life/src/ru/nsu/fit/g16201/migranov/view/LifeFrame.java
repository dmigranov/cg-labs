package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;

import ru.nsu.fit.g16201.migranov.controller.Controller;

import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;



//public class LifeFrame extends JFrame {
public class LifeFrame extends MainFrame {
    private FieldPanel fieldPanel;
    private Controller controller;

    private File currentFile = null;

    private JButton clearButton, parametersButton, stepButton;

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

                controller.setRunning(false);
                int result = JOptionPane.showConfirmDialog(LifeFrame.this, "Do you want to save the current state of field?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(result == JOptionPane.YES_OPTION) {
                    onSave();
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
                else if(result == JOptionPane.NO_OPTION)
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                else
                    controller.run();
            }
        });

        JMenuItem item;
        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/New", "New field", KeyEvent.VK_N, "reload.png", "onNew");
        addMenuItem("File/Open", "Open a field description file", KeyEvent.VK_O, "upload-1.png", "onOpen");//
        addMenuItem("File/Save", "Save a field state", KeyEvent.VK_S, "download.png", "onSave");//
        item = (JMenuItem)getMenuElement("File/Save");
        item.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        addMenuItem("File/Save As", "Save a field state as", KeyEvent.VK_A, "download-1.png", "onSaveAs");//
        addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "logout.png", "onExit");

        addSubMenu("Edit", KeyEvent.VK_E);
        JMenu editMenu = (JMenu)getMenuElement("Edit");
        ButtonGroup group = new ButtonGroup();
        addRadioButtonMenuItem(editMenu, "Replace", "Replace mode", KeyEvent.VK_R, "edit.png", group, true, "onReplace");
        addRadioButtonMenuItem(editMenu,"XOR", "XOR mode", KeyEvent.VK_X, "shuffle.png", group, false, "onXOR");
        addToolBarToggleButton("Edit/Replace");
        addToolBarToggleButton("Edit/XOR");
        addMenuSeparator("Edit");
        addMenuItem("Edit/Clear", "Clear the field", KeyEvent.VK_C, "cancel.png", "onClear");
        clearButton = createToolBarButton("Edit/Clear");
        toolBar.add(clearButton);
        addMenuSeparator("Edit");
        addMenuItem("Edit/Parameters", "Change the field parameters", KeyEvent.VK_P, "settings.png", "onParameters");

        addToolBarSeparator();
        parametersButton = createToolBarButton("Edit/Parameters");
        toolBar.add(parametersButton);

        addSubMenu("Game", KeyEvent.VK_G);
        JMenu gameMenu = (JMenu)getMenuElement("Game");
        addMenuItem("Game/Step", "Next step", KeyEvent.VK_S, "next.png", "onStep");
        addCheckBoxMenuItem(gameMenu, "Run", "Run step-by-step execution", KeyEvent.VK_R, "next-1.png", false, "onRun");

        addSubMenu("View", KeyEvent.VK_V);
        JMenu viewMenu = (JMenu)getMenuElement("View");
        addCheckBoxMenuItem(viewMenu, "Show impacts", "Indicates whether impacts should be shown", KeyEvent.VK_I, "magnifying-glass.png", false, "onShowImpacts");

        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About", "Shows program version and copyright information", KeyEvent.VK_A, "book.png", "onAbout");

        addToolBarSeparator();
        stepButton = createToolBarButton("Game/Step");
        toolBar.add(stepButton);
        addToolBarToggleButton("Game/Run");
        addToolBarSeparator();
        addToolBarToggleButton("Help/About");

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
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
        aboutPanel.add(new JLabel("Made as a part of NSU Computer Graphics course"));
        aboutPanel.add(new JLabel("Denis Migranov, group 16201, 2019"));
        aboutPanel.add(new JLabel("Icons used are from www.flaticon.com/packs/multimedia-collection"));
        JOptionPane.showMessageDialog(this, aboutPanel, "About FIT_16201_Migranov_Life", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onExit()
    {
        controller.setRunning(false);
        int result = JOptionPane.showConfirmDialog(LifeFrame.this, "Do you want to save the current state of field?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(result == JOptionPane.YES_OPTION) {
            onSave();
            System.exit(0);
        }
        else if(result == JOptionPane.NO_OPTION)
            System.exit(0);
        else
            controller.run();

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
        //todo: индикатор изменений в fieldpanel, чтобы если ничего не было изменено, не пересправшивать (третий порядок впжности)
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

        JTextField mField = new JTextField(3);
        JTextField nField = new JTextField(3);

        mField.addKeyListener(new IntegerTextFieldKeyListener());
        nField.addKeyListener(new IntegerTextFieldKeyListener());

        JPanel mnPanel = new JPanel();
        mnPanel.add(new JLabel("m: "));
        mnPanel.add(mField);
        mnPanel.add(Box.createHorizontalStrut(10));
        mnPanel.add(new JLabel("n: "));
        mnPanel.add(nField);

        if(JOptionPane.showConfirmDialog(this, mnPanel, "Field parameters", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            String mText = mField.getText();
            String nText = nField.getText();
            if(!"".equals(mText) && !"".equals(nText))
            {
                int m = Integer.parseInt(mText);
                int n = Integer.parseInt(nText);
                controller.setField(m, n);
            }
            //я думаю, тут мы поле совсем очищаем. а вот при изменении параметров - надо ужимать.расширять по возможности сохраняя состояние
        }
    }

    public void onParameters()
    {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));


        JPanel mnPanel = new JPanel();
        JTextField mField = new JTextField(3);
        JTextField nField = new JTextField(3);
        mField.addKeyListener(new IntegerTextFieldKeyListener());
        nField.addKeyListener(new IntegerTextFieldKeyListener());
        mnPanel.add(new JLabel("m: "));
        mnPanel.add(mField);
        mnPanel.add(Box.createHorizontalStrut(10));
        mnPanel.add(new JLabel("n: "));
        mnPanel.add(nField);

        optionsPanel.add(mnPanel);
        //тут надо ужимать/расширять поле, а не как в нью

        JPanel kPanel = new JPanel();
        int kMin = 5, kMax = 50;
        JSlider kSlider = new JSlider(JSlider.HORIZONTAL, kMin, kMax, kMin);
        JTextField kField = new JTextField(kMin + "",2);

        kField.addKeyListener(new IntegerTextFieldKeyListenerWithSlider(kSlider));

        kSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int k = kSlider.getValue();
                kField.setText(k+ "");
            }
        });
        //JTextField wField = new JTextField(3);
        kPanel.add(new JLabel("k: "));
        kPanel.add(kField);
        kPanel.add(Box.createHorizontalStrut(10));
        kPanel.add(kSlider);
        optionsPanel.add(kPanel);


        JOptionPane.showConfirmDialog(this, optionsPanel, "Field and visualisation parameters", JOptionPane.OK_CANCEL_OPTION);
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
        ((JMenuItem)getMenuElement("File/New")).setEnabled(isActive);
        ((JMenuItem)getMenuElement("File/Save")).setEnabled(isActive);
        ((JMenuItem)getMenuElement("File/Open")).setEnabled(isActive);
        ((JMenuItem)getMenuElement("File/Save As")).setEnabled(isActive);
        ((JMenuItem)getMenuElement("Game/Step")).setEnabled(isActive);
        ((JMenuItem)getMenuElement("Edit/Clear")).setEnabled(isActive);
        ((JMenuItem)getMenuElement("Edit/Parameters")).setEnabled(isActive);
        clearButton.setEnabled(isActive);
        parametersButton.setEnabled(isActive);
        stepButton.setEnabled(isActive);
    }
}
