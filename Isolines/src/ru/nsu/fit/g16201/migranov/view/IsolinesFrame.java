package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.controller.Controller;
import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class IsolinesFrame extends MainFrame {
    private JLabel statusLabel = new JLabel("");

    private List<AbstractButton> deactivatedButtons = new ArrayList<>();
    private Controller controller;

    public static void main(String[] args) throws Exception {
        new IsolinesFrame();
    }

    private IsolinesFrame() throws Exception {
        super(800, 600, "Untitled | Denis Migranov, 16201");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        //Box legendBox = Box.createHorizontalBox();
        //Box mapBox = Box.createHorizontalBox();

        MapPanel mapPanel = new MapPanel(500, 500);
        LegendPanel legendPanel = new LegendPanel(500, 150, 100);
        controller = new Controller(mapPanel, legendPanel, statusLabel);

        mainPanel.setDoubleBuffered(true);
        mapPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        mapPanel.setPreferredSize(new Dimension(500, 500));
        legendPanel.setPreferredSize(new Dimension(500, 150));

        mainPanel.add(mapPanel, BorderLayout.CENTER);
        mainPanel.add(legendPanel, BorderLayout.SOUTH);
        mainPanel.add(Box.createHorizontalStrut(20), BorderLayout.WEST);
        mainPanel.add(Box.createHorizontalStrut(20), BorderLayout.EAST);
        add(mainPanel);

        addMenus();

        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        revalidate();
        setMinimumSize(new Dimension(1200, 900));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addMenus() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuAndToolBarButton("File/Open", "Open a file", KeyEvent.VK_O, "upload-1.png", "onOpen", false);
        addMenuAndToolBarButton("File/Exit", "Exit the aplication", KeyEvent.VK_E, "logout.png", "onExit", false);

        addSubMenu("Options", KeyEvent.VK_O);
        addMenuAndToolBarButton("Options/Parameters", "Change parameters", KeyEvent.VK_P, "settings.png", "onParameters", true);

        ButtonGroup group = new ButtonGroup();
        addRadioButtonMenuAndToolBarButton("Options/Interpolation mode", "Shows if interpolation is enabled", KeyEvent.VK_I, "bw.png", group, "onInterpolationEnabled", false, true);
        addRadioButtonMenuAndToolBarButton("Options/Filled color map mode", "Shows if filling mode is enabled", KeyEvent.VK_F, "absorption.png", group, "onFillEnabled", true, true);
        addRadioButtonMenuAndToolBarButton("Options/Per-pixel color map mode", "Shows if per-pixel color map is enabled", KeyEvent.VK_C, "emboss.png", group, "onPerPixelColorMap", false, true);

        addCheckBoxMenuAndToolBarButton("Options/Isolines on", "Shows if isolines are shown", KeyEvent.VK_L, "line.png", "onIsolinesEnabled", true, true);
        addCheckBoxMenuAndToolBarButton("Options/Grid on", "Shows if grid is shown", KeyEvent.VK_G, "grid.png", "onGridEnabled", false, true);
        addCheckBoxMenuAndToolBarButton("Options/Grid points on", "Shows if grid points are shown", KeyEvent.VK_P, "gridpoint.png", "onPointsEnabled", false, true);
        addMenuAndToolBarButton("Options/Clear user isolines", "Clear user isolines", KeyEvent.VK_C, "clear.png", "onClear", true);

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
                method.invoke(IsolinesFrame.this);
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
                    method.invoke(IsolinesFrame.this);
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

    private void addRadioButtonMenuAndToolBarButton(String path, String tooltip, int mnemonic, String icon, ButtonGroup group,  String actionMethod, boolean state, boolean isDeactivated) throws NoSuchMethodException
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
                    method.invoke(IsolinesFrame.this);
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

        JToggleButton button = new JToggleButton(item.getIcon());

        if(icon != null)
            button.setIcon(new ImageIcon(getClass().getResource("resources/"+icon), title));
        button.setToolTipText(item.getToolTipText());
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

    public void onOpen()
    {
        File file = getOpenFileName("txt", "A function description file");
        if(file != null) {
            setTitle(file.getName() + " | Denis Migranov, 16201");
            int r = controller.loadFile(file);
            if(r == 0)
            {
                for (AbstractButton b : deactivatedButtons)
                {
                    b.setEnabled(true);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Wrong file format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onExit()
    {
        System.exit(0);
    }

    public  void onParameters()
    {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        optionsPanel.setLayout(new GridLayout(6, 2));
        JTextField kField = new JTextField(controller.getK() + "",4);
        kField.addKeyListener(new IntegerTextFieldKeyListener());
        JTextField mField = new JTextField(controller.getM() + "",4);
        mField.addKeyListener(new IntegerTextFieldKeyListener());

        optionsPanel.add(new JLabel("k: "));
        optionsPanel.add(kField);
        optionsPanel.add(new JLabel("m: "));
        optionsPanel.add(mField);

        double[] constList = controller.getRegionSizes();
        List<JTextField> regionSizesFields = new ArrayList<>();
        for(int i = 0; i < constList.length; i++) {
            regionSizesFields.add(new JTextField("" + constList[i]));
            regionSizesFields.get(i).addKeyListener(new FloatTextFieldKeyListener());
        }

        optionsPanel.add(new JLabel("a: "));
        optionsPanel.add(regionSizesFields.get(0));
        optionsPanel.add(new JLabel("b: "));
        optionsPanel.add(regionSizesFields.get(1));
        optionsPanel.add(new JLabel("c: "));
        optionsPanel.add(regionSizesFields.get(2));
        optionsPanel.add(new JLabel("d: "));
        optionsPanel.add(regionSizesFields.get(3));

        if(JOptionPane.showConfirmDialog(this, optionsPanel, "Region and marching squares parameters", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            try
            {
                int k, m;
                double a, b, c, d;
                k = Integer.parseInt(kField.getText());
                m = Integer.parseInt(mField.getText());
                a = Double.parseDouble(regionSizesFields.get(0).getText());
                b = Double.parseDouble(regionSizesFields.get(1).getText());
                c = Double.parseDouble(regionSizesFields.get(2).getText());
                d = Double.parseDouble(regionSizesFields.get(3).getText());

                if(a >= b || c >= d || k <= 2 || m <= 2 || m > 300 || k > 300)
                    throw new NumberFormatException();

                controller.setModelConstants(k, m, a, b, c, d);
            }
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(this, "Wrong constants", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public  void onIsolinesEnabled()
    {
        controller.setIsolinesEnabled(!controller.areIsolinesEnabled());
    }

    public void onGridEnabled()
    {
        controller.setGridEnabled(!controller.isGridEnabled());
    }

    public void onPointsEnabled()
    {
        controller.setGridPointsEnabled(!controller.areGridPointsEnabled());
    }

    public void onInterpolationEnabled()
    {
        //controller.setInterpolationEnabled(!controller.isInterpolationEnabled());
        controller.setMode(Controller.INTERPOLATION);

    }

    public void onPerPixelColorMap()
    {
        //controller.setPerPixelColorMapEnabled(!controller.isPerPixelColorMapEnabled());
        controller.setMode(Controller.PERPIXELACTUAL);

    }

    public void onFillEnabled()
    {
        controller.setMode(Controller.SPAN);
    }

    public void onClear()
    {
        controller.clearUserIsolines();
    }

    public void onAbout()
    {
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
        aboutPanel.add(new JLabel("Made as a part of NSU Computer Graphics course"));
        aboutPanel.add(new JLabel("Denis Migranov, group 16201, 2019"));
        aboutPanel.add(new JLabel("Icons used are from www.flaticon.com/packs/multimedia-collection and icons8.com"));
        JOptionPane.showMessageDialog(this, aboutPanel, "About FIT_16201_Migranov_Isolines", JOptionPane.INFORMATION_MESSAGE);
    }
}
