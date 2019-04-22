package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.controller.Controller;
import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class WireframeFrame extends MainFrame {
    private JLabel statusLabel = new JLabel("");
    private List<AbstractButton> deactivatedButtons = new ArrayList<>();

    private Controller controller;

    private SplinePanel splinePanel;
    private JPanel splineConfigurationPanel, mainPanel;
    private JTabbedPane tabbedPane;

    private WireframePanel wireframePanel;

    private JTextField aField, bField, cField, dField, nField, mField, kField, aSplineField, bSplineField, swField, shField, znField, zfField;
    private JButton confirmButton, addFigureButton;
    private int figureCount;
    private boolean fileIsLoaded = false;

    private ButtonGroup group;

    public static void main(String[] args) throws Exception {
        new WireframeFrame();
    }

    private WireframeFrame() throws Exception {
        super(800, 600, "Untitled | Denis Migranov, 16201");

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resize();
            }
        });
        wireframePanel = new WireframePanel();
        splinePanel = new SplinePanel(501, 501);
        mainPanel.add(wireframePanel);
        controller = new Controller(splinePanel, wireframePanel);
        addMenus();

        add(mainPanel);

        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void resize() {
        if(!fileIsLoaded)
            return;
        int width = mainPanel.getWidth();
        int height = mainPanel.getHeight();
        double sw = controller.getSw(), sh = controller.getSh();

        double nwidth, nheight;
        if(width < height) {
            nheight = height;
            nwidth = sw / sh * height;
            if(nwidth > width)
            {
                nheight = nheight / nwidth * width;
                nwidth = width;
            }
        }
        else
        {
            nwidth = width;
            nheight = sh / sw * width;
            if(nheight > height)
            {
                nwidth = nwidth/ nheight * height;
                nheight = height;
            }
        }
        wireframePanel.setPreferredSize(new Dimension((int)Math.round(nwidth) - 20, (int)Math.round(nheight) - 20));
        controller.drawFigures();
        mainPanel.revalidate();
    }

    private void createCommonConfigurationPanel() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            private final Insets borderInsets = new Insets(0, 0, 0, 0);
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }
            @Override
            protected Insets getContentBorderInsets(int tabPlacement) {
                return borderInsets;
            }
        });     //без этого были странные белые полоски под вкладками https://stackoverflow.com/questions/5183687/java-remove-margin-padding-on-a-jtabbedpane
        JPanel commonPanel = new JPanel();
        tabbedPane.add("Common", commonPanel);
        createSplineConfigurationPanel();
        for(int i = 0; i < figureCount; i++) {
            JPanel panel = new JPanel();
            panel.setPreferredSize(splineConfigurationPanel.getPreferredSize());
            tabbedPane.add("Figure " + (i + 1), panel);
        }
        tabbedPane.addChangeListener(e -> {
            //0 - common
            int selected = tabbedPane.getSelectedIndex();
            if(selected != 0) {
                JPanel panel = ((JPanel)tabbedPane.getSelectedComponent());
                panel.add(splineConfigurationPanel);
                panel.revalidate();
                controller.setCurrentFigure(selected - 1);
            }
        });

        commonPanel.setLayout(new GridLayout(3, 4));    //todo: сделать красиво

        aField = new JTextField();
        bField = new JTextField();
        cField = new JTextField();
        dField = new JTextField();
        nField = new JTextField();
        mField = new JTextField();
        kField = new JTextField();
        swField = new JTextField();
        shField = new JTextField();
        zfField = new JTextField();
        znField = new JTextField();
        commonPanel.add(new LabelTextField("a: ", aField, new FloatTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("b: ", bField, new FloatTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("c: ", cField, new FloatTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("d: ", dField, new FloatTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("n: ", nField, new IntegerTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("m: ", mField, new IntegerTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("k: ", kField, new IntegerTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("sw: ", swField, new FloatTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("sh: ", shField, new FloatTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("Zfar: ", zfField, new FloatTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("Znear: ", znField, new FloatTextFieldKeyListener()));

        addFigureButton = new JButton("Add a new figure");
        addFigureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //todo
            }
        });
        //commonPanel.add(addFigureButton);

        confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> {
            try
            {
                if(tabbedPane.getSelectedIndex() == 0) {
                    double a, b, c, d;
                    int n, m, k;
                    n = Integer.parseInt(nField.getText());
                    k = Integer.parseInt(kField.getText());
                    m = Integer.parseInt(mField.getText());
                    a = Double.parseDouble(aField.getText());
                    b = Double.parseDouble(bField.getText());
                    c = Double.parseDouble(cField.getText());
                    d = Double.parseDouble(dField.getText());

                    if(m <= 0 || n <= 0 || k <= 0)
                        throw new NumberFormatException("Wrong m, n, or k");
                    if (!(b > a && a >= 0 && 1 >= b))
                        throw new NumberFormatException("Wrong a or b");
                    if (!(d > c && c >= 0 && 2 * Math.PI >= d))
                        throw new NumberFormatException("Wrong c or d");
                    //todo: clipping const

                    controller.setConstants(n, m, k, a, b, c, d);
                }
                else
                {
                    double a, b;
                    a = Double.parseDouble(aSplineField.getText());
                    b = Double.parseDouble(bSplineField.getText());

                    if (!(b > a && a >= 0 && 1 >= b))
                        throw new NumberFormatException("Wrong a or b");
                    controller.setAB(a, b);
                }
                updateFields();
            }
            catch (NumberFormatException n)
            {
                JOptionPane.showMessageDialog(WireframeFrame.this, n.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void addMenus() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuAndToolBarButton("File/Open", "Open a file", KeyEvent.VK_O, "upload-1.png", "onOpen", false);
        addMenuAndToolBarButton("File/Save as", "Save figures as", KeyEvent.VK_S, "download.png", "onSave", true);

        addSubMenu("Options", KeyEvent.VK_O);
        addMenuAndToolBarButton("Options/Configuration", "Configure splines", KeyEvent.VK_S, "upload-1.png", "onConfigureSplines", true);

        addSubMenu("Help", KeyEvent.VK_H);
        addMenuAndToolBarButton("Help/About", "Shows program version and copyright information", KeyEvent.VK_A, "book.png", "onAbout", false);
    }

    private void addMenuAndToolBarButton(String path, String tooltip, int mnemonic, String icon, String actionMethod, boolean isDeactivated) throws NoSuchMethodException
    {
        MenuElement element = getParentMenuElement(path);
        if(element == null)
            throw new InvalidParameterException("Menu path not found: " + path);
        String title = getMenuPathName(path);
        JMenuItem item = new JMenuItem(title);
        item.setMnemonic(mnemonic);
        item.setToolTipText(tooltip);
        item.addMouseListener(new StatusTitleListener(statusLabel));
        final Method method = getClass().getMethod(actionMethod);
        item.addActionListener(evt -> {
            try {
                method.invoke(WireframeFrame.this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        if(element instanceof JMenu)
            ((JMenu)element).add(item);
        else if(element instanceof JPopupMenu)
            ((JPopupMenu)element).add(item);
        else
            throw new InvalidParameterException("Invalid menu path: " + path);

        JButton button = new JButton();
        if(icon != null)
            button.setIcon(new ImageIcon(getClass().getResource("resources/"+icon), title));
        for(ActionListener listener: item.getActionListeners())
            button.addActionListener(listener);
        button.setToolTipText(tooltip);
        button.addMouseListener(new StatusTitleListener(statusLabel));
        toolBar.add(button);
        if(isDeactivated)
        {
            item.setEnabled(false);
            button.setEnabled(false);
            deactivatedButtons.add(item);
            deactivatedButtons.add(button);
        }
    }

    private void addCheckBoxMenuAndToolBarButton(String path, String tooltip, int mnemonic, String icon, String actionMethod, boolean state, boolean isDeactivated) throws NoSuchMethodException
    {
        MenuElement element = getParentMenuElement(path);
        if(element == null)
            throw new InvalidParameterException("Menu path not found: " + path);
        String title = getMenuPathName(path);

        JCheckBoxMenuItem item = new JCheckBoxMenuItem(title, state);

        item.setMnemonic(mnemonic);
        item.setToolTipText(tooltip);

        item.addMouseListener(new StatusTitleListener(statusLabel));

        final Method method = getClass().getMethod(actionMethod);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    method.invoke(WireframeFrame.this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        if(element instanceof JMenu)
            ((JMenu)element).add(item);
        else if(element instanceof JPopupMenu)
            ((JPopupMenu)element).add(item);
        else
            throw new InvalidParameterException("Invalid menu path: " + path);

        JToggleButton button = new JToggleButton();
        if(icon != null)
            button.setIcon(new ImageIcon(getClass().getResource("resources/"+icon), title));
        button.setToolTipText(tooltip);
        button.setModel(item.getModel());   //кнопки повторяют поведение меню, включая "зажатость"
        toolBar.add(button);
        button.addMouseListener(new StatusTitleListener(statusLabel));

        if(isDeactivated)
        {
            item.setEnabled(false);
            button.setEnabled(false);
            deactivatedButtons.add(item);
            deactivatedButtons.add(button);
        }
    }

    private void addRadioButtonMenuAndToolBarButton(String path, String tooltip, int mnemonic, String icon, ButtonGroup group,  String actionMethod, boolean state, boolean isDeactivated, boolean areToolBarButtonsAdded) throws NoSuchMethodException
    {
        MenuElement element = getParentMenuElement(path);
        if(element == null)
            throw new InvalidParameterException("Menu path not found: " + path);
        String title = getMenuPathName(path);

        JRadioButtonMenuItem item = new JRadioButtonMenuItem(title, state);

        item.setMnemonic(mnemonic);
        item.setToolTipText(tooltip);

        item.addMouseListener(new StatusTitleListener(statusLabel));

        final Method method = getClass().getMethod(actionMethod);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    method.invoke(WireframeFrame.this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        if(element instanceof JMenu)
            ((JMenu)element).add(item);
        else if(element instanceof JPopupMenu)
            ((JPopupMenu)element).add(item);
        else
            throw new InvalidParameterException("Invalid menu path: " + path);

        group.add(item);

        if(areToolBarButtonsAdded) {
            JToggleButton button = new JToggleButton(item.getIcon());

            if (icon != null)
                button.setIcon(new ImageIcon(getClass().getResource("resources/" + icon), title));
            button.setToolTipText(item.getToolTipText());
            button.setModel(item.getModel());   //кнопки повторяют поведение меню, включая "зажатость"
            toolBar.add(button);
            button.addMouseListener(new StatusTitleListener(statusLabel));
            if(isDeactivated)
            {
                button.setEnabled(false);
                deactivatedButtons.add(button);
            }
        }

        if(isDeactivated)
        {
            item.setEnabled(false);
            deactivatedButtons.add(item);
        }
    }

    private void createFigureChoiceMenu() throws NoSuchMethodException {
        if(menuBar.getMenuCount() > 3)
            menuBar.remove(3);
        addSubMenu("Rotation", KeyEvent.VK_F);
        int key = KeyEvent.VK_A;
        group = new ButtonGroup();
        addRadioButtonMenuAndToolBarButton("Rotation/World", "Choose what to rotate", key++,"rotate.png", group, "onRotateChoose", true, false, false);
        for (int i = 0; i < figureCount; i++)
        {
            addRadioButtonMenuAndToolBarButton("Rotation/Figure " + (i+1), "Choose what to rotate", key++,"rotate.png", group, "onRotateChoose", false, false, false);
        }
    }

    private void updateFields() {
        aField.setText(controller.getA() + "");
        aSplineField.setText(controller.getA() + "");
        bField.setText(controller.getB() + "");
        bSplineField.setText(controller.getB() + "");
        cField.setText(controller.getC() + "");
        dField.setText(controller.getD() + "");
        nField.setText(controller.getN() + "");
        mField.setText(controller.getM() + "");
        kField.setText(controller.getK() + "");
    }

    public void createSplineConfigurationPanel()
    {
        splineConfigurationPanel = new JPanel();   //tabs...
        splineConfigurationPanel.add(splinePanel);
        JPanel inputPanel = new JPanel(), inputButtonPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(7, 2, 3, 5));
        inputButtonPanel.setLayout(new BoxLayout(inputButtonPanel, BoxLayout.Y_AXIS));
        inputButtonPanel.add(inputPanel);

        splineConfigurationPanel.add(inputButtonPanel);
        JButton zoomInButton = new JButton("Zoom in");
        JButton zoomOutButton = new JButton("Zoom out");
        zoomInButton.addActionListener(e -> controller.changeScale(-0.2));
        zoomOutButton.addActionListener(e -> controller.changeScale(0.2));
        JButton addFirstPointButton = new JButton("Add new point in the beginning");
        addFirstPointButton.addActionListener(e -> {controller.addSplinePoint(0); controller.drawFigures();});
        JButton addLastPointButton = new JButton("Add new point in the end");
        addLastPointButton.addActionListener(e -> {controller.addSplinePoint(controller.getSplinePointsCount()); controller.drawFigures();});
        JButton deleteFirstPointButton = new JButton("Delete the point in the beginning");
        deleteFirstPointButton.addActionListener(e -> {controller.deleteSplinePoint(0); controller.drawFigures();});
        JButton deleteLastPointButton = new JButton("Delete the point in the end");
        deleteLastPointButton.addActionListener(e -> {controller.deleteSplinePoint(controller.getSplinePointsCount() - 1); controller.drawFigures();});

        aSplineField = new JTextField();
        bSplineField = new JTextField();
        aSplineField.addKeyListener(new FloatTextFieldKeyListener());
        bSplineField.addKeyListener(new FloatTextFieldKeyListener());

        inputPanel.add(new JLabel("a: "));
        inputPanel.add(aSplineField);
        inputPanel.add(new JLabel("b: "));
        inputPanel.add(bSplineField);
        inputPanel.add(addFirstPointButton);
        inputPanel.add(addLastPointButton);
        inputPanel.add(deleteFirstPointButton);
        inputPanel.add(deleteLastPointButton);
        inputPanel.add(zoomInButton);
        inputPanel.add(zoomOutButton);

        JButton deleteFigureButton = new JButton("Delete this figure");
        deleteFigureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = tabbedPane.getSelectedIndex();
                controller.deleteFigure(selected - 1);
                tabbedPane.remove(selected);
            }
        });

        inputButtonPanel.add(deleteFigureButton);
    }

    public void onOpen() throws NoSuchMethodException
    {
        File file = getOpenFileName("txt", "A function description file");
        if(file != null) {
            setTitle(file.getName() + " | Denis Migranov, 16201");
            int r = controller.loadFile(file);
            if(r > 0)
            {
                figureCount = r;
                createCommonConfigurationPanel();
                for (AbstractButton b : deactivatedButtons)
                {
                    b.setEnabled(true);
                }
                fileIsLoaded = true;
                createFigureChoiceMenu();
                wireframePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                resize();
            }
            else
            {
                wireframePanel.setBorder(BorderFactory.createEmptyBorder());

                fileIsLoaded = false;
                JOptionPane.showMessageDialog(this, "Wrong file format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onSave()
    {
        File file = getSaveFileName("png", "A PNG file");
        if (file != null) {
            controller.saveFile(file);
        }
    }

    public void onAbout()
    {
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
        aboutPanel.add(new JLabel("Made as a part of NSU Computer Graphics course"));
        aboutPanel.add(new JLabel("Denis Migranov, group 16201, 2019"));
        aboutPanel.add(new JLabel("Icons used are from www.flaticon.com/packs/multimedia-collection and icons8.com"));
        JOptionPane.showMessageDialog(this, aboutPanel, "About FIT_16201_Migranov_Wireframe", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onConfigureSplines()
    {
        updateFields();

        //JOptionPane.showOptionDialog(this, configurationPanel, "Configuration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        JOptionPane.showOptionDialog(this, tabbedPane, "Configuration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{confirmButton, addFigureButton}, confirmButton);
    }

    public void onRotateChoose()
    {
        int i = 0;
        for(Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements(); i++)
        {
            AbstractButton button = buttons.nextElement();
            if(button.isSelected())
                break;
        }
        controller.setCurrentRotateFigure(i-1);
    }
}
