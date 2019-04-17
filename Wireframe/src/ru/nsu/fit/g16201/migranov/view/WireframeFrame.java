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
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class WireframeFrame extends MainFrame {
    private JLabel statusLabel = new JLabel("");
    private List<AbstractButton> deactivatedButtons = new ArrayList<>();

    private Controller controller;

    private SplinePanel splinePanel;
    private JPanel configurationPanel;
    private JTabbedPane tabbedPane = new JTabbedPane();


    private JTextField aField, bField, cField, dField, nField, mField, kField;
    private JButton confirmButton;

    public static void main(String[] args) throws Exception {
        new WireframeFrame();
    }

    private WireframeFrame() throws Exception {
        super(800, 600, "Untitled | Denis Migranov, 16201");

        splinePanel = new SplinePanel(501, 501);
        controller = new Controller(splinePanel);
        createConfigurationPanel();
        addMenus();

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

    private void createConfigurationPanel() {
        configurationPanel = new JPanel();   //tabs...
        JPanel commonPanel = new JPanel();
        tabbedPane.add("Common", commonPanel);
        tabbedPane.add("Figure 1", configurationPanel);

        commonPanel.setLayout(new BoxLayout(commonPanel, BoxLayout.Y_AXIS));
        configurationPanel.add(splinePanel);
        JPanel inputPanel = new JPanel(), inputButtonPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 3, 5));
        inputButtonPanel.setLayout(new BoxLayout(inputButtonPanel, BoxLayout.Y_AXIS));
        inputButtonPanel.add(inputPanel);
        configurationPanel.add(inputButtonPanel);

        JButton zoomInButton = new JButton("Zoom in");
        JButton zoomOutButton = new JButton("Zoom out");
        zoomInButton.addActionListener(e -> controller.changeScale(-0.2));
        zoomOutButton.addActionListener(e -> controller.changeScale(0.2));

        JButton addFirstPointButton = new JButton("Add new point in the beginning");
        addFirstPointButton.addActionListener(e -> controller.addSplinePoint(0));
        JButton addLastPointButton = new JButton("Add new point in the end");
        addLastPointButton.addActionListener(e -> controller.addSplinePoint(controller.getSplinePointsCount()));
        JButton deleteFirstPointButton = new JButton("Delete the point in the beginning");
        deleteFirstPointButton.addActionListener(e -> controller.deleteSplinePoint(0));
        JButton deleteLastPointButton = new JButton("Delete the point in the end");
        deleteLastPointButton.addActionListener(e -> controller.deleteSplinePoint(controller.getSplinePointsCount() - 1));

        aField = new JTextField();
        bField = new JTextField();
        cField = new JTextField();
        dField = new JTextField();
        nField = new JTextField();
        mField = new JTextField();
        kField = new JTextField();

        /*aField.addKeyListener(new FloatTextFieldKeyListener());
        bField.addKeyListener(new FloatTextFieldKeyListener());
        cField.addKeyListener(new FloatTextFieldKeyListener()); //может лучше ползунок для угла?
        dField.addKeyListener(new FloatTextFieldKeyListener());
        nField.addKeyListener(new IntegerTextFieldKeyListener());
        mField.addKeyListener(new IntegerTextFieldKeyListener());
        kField.addKeyListener(new IntegerTextFieldKeyListener());*/


        /*commonPanel.add(new JLabel("a: "));
        commonPanel.add(aField);
        commonPanel.add(new JLabel("b: "));
        commonPanel.add(bField);
        commonPanel.add(new JLabel("c: "));
        commonPanel.add(cField);
        commonPanel.add(new JLabel("d: "));
        commonPanel.add(dField);
        commonPanel.add(new JLabel("n: "));
        commonPanel.add(nField);
        commonPanel.add(new JLabel("m: "));
        commonPanel.add(mField);
        commonPanel.add(new JLabel("k: "));
        commonPanel.add(kField);*/
        commonPanel.add(new LabelTextField("a: ", aField, new FloatTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("b: ", bField, new FloatTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("c: ", cField, new FloatTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("d: ", dField, new FloatTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("n: ", nField, new IntegerTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("m: ", mField, new IntegerTextFieldKeyListener()));
        commonPanel.add(new LabelTextField("k: ", kField, new IntegerTextFieldKeyListener()));

        inputPanel.add(addFirstPointButton);
        inputPanel.add(addLastPointButton);
        inputPanel.add(deleteFirstPointButton);
        inputPanel.add(deleteLastPointButton);
        inputPanel.add(zoomInButton);
        inputPanel.add(zoomOutButton);

        confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> {
            try
            {
                double a, b, c, d;
                int n, m, k;
                n = Integer.parseInt(nField.getText());
                k = Integer.parseInt(kField.getText());
                m = Integer.parseInt(mField.getText());
                a = Double.parseDouble(aField.getText());
                b = Double.parseDouble(bField.getText());
                c = Double.parseDouble(cField.getText());
                d = Double.parseDouble(dField.getText());

                if(!(b > a && a >= 0 && 1 >= b))
                    throw new NumberFormatException("Wrong a or b");

                if(!(d > c && c >= 0 && 2*Math.PI >= d))
                    throw new NumberFormatException("Wrong c or d");

                controller.setConstants(n, m, k, a, b, c, d);
            }
            catch (NumberFormatException n)
            {
                JOptionPane.showMessageDialog(WireframeFrame.this, n.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        //inputButtonPanel add confirmButton

        //todo: масштрабирование? (я думаю, просто поменять коэфф. с 1.1 на что-то иное!
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
        //splinePanel - непосредственно для отрисовки, кнопки в другом

        aField.setText(controller.getA() + "");
        bField.setText(controller.getB() + "");
        cField.setText(controller.getC() + "");
        dField.setText(controller.getD() + "");
        nField.setText(controller.getN() + "");
        mField.setText(controller.getM() + "");
        kField.setText(controller.getK() + "");

        //JOptionPane.showOptionDialog(this, configurationPanel, "Configuration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        JOptionPane.showOptionDialog(this, tabbedPane, "Configuration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{confirmButton}, confirmButton);

    }
}
