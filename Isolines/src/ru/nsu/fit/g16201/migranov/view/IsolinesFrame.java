package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.controller.Controller;
import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
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
        //todo: u0v0 и u1v1 фиксированные? спросить
        super(800, 600, "Untitled | Denis Migranov, 16201");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        Box legendBox = Box.createHorizontalBox();
        Box mapBox = Box.createHorizontalBox();
        //mainPanel.setPreferredSize(new Dimension(500, 660));
        //mainPanel.setMaximumSize(new Dimension(500, 660));

        MapPanel mapPanel = new MapPanel(500, 500);
        LegendPanel legendPanel = new LegendPanel(500, 150);
        controller = new Controller(mapPanel, legendPanel);

        legendBox.add(Box.createHorizontalStrut(10));
        legendBox.add(legendPanel);
        legendBox.add(Box.createHorizontalGlue());
        legendBox.setPreferredSize(new Dimension(510, 150));
        legendBox.setMaximumSize(new Dimension(510, 150));
        legendBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        mapBox.add(Box.createHorizontalStrut(10));
        mapBox.add(mapPanel);
        mapBox.add(Box.createHorizontalGlue());
        mapBox.setPreferredSize(new Dimension(510, 500));
        mapBox.setMaximumSize(new Dimension(510, 500));
        mapBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        mapPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        mapPanel.setPreferredSize(new Dimension(500, 500)); //todo: ?

        legendPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        legendPanel.setPreferredSize(new Dimension(500, 100));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(mapBox);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(legendBox);



        addMenus();

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setDoubleBuffered(true);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);

        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        revalidate();
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addMenus() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuAndToolBarButton("File/Open", "Open a file", KeyEvent.VK_O, "upload-1.png", "onOpen", false);
        addMenuAndToolBarButton("File/Exit", "Exit the aplication", KeyEvent.VK_E, "logout.png", "onExit", false);


        addSubMenu("Options", KeyEvent.VK_O);
        addCheckBoxMenuAndToolBarButton("Options/Interpolation on", "Shows if interpolation is enabled", KeyEvent.VK_I, "blur.png", "onInterpolationEnabled", false, false);
        addMenuAndToolBarButton("Options/Parameters", "Change parameters", KeyEvent.VK_P, "settings.png", "onParameters", false);
        addCheckBoxMenuAndToolBarButton("Options/Isolines on", "Shows if isolines are shown", KeyEvent.VK_L, "blur.png", "onIsolinesEnabled", true, false);
        addCheckBoxMenuAndToolBarButton("Options/Grid on", "Shows if grid is shown", KeyEvent.VK_G, "blur.png", "onGridEnabled", false, false);


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
                    //throw new RuntimeException(e);
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

    public void onOpen()
    {
        File file = getOpenFileName("txt", "A function description file");
        if(file != null) {
            setTitle(file.getName() + " | Denis Migranov, 16201");
            int r = controller.loadFile(file);
            if(r == 0)
            {
                /*for (AbstractButton b : deactivatedButtons)
                {
                    b.setEnabled(true);
                }*/
                controller.drawMap();
                controller.drawLegend();
                //revalidate?

            }
            else
            {
                JOptionPane.showMessageDialog(null, "Wrong file format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onExit()
    {
        System.exit(0);
    }

    public  void onInterpolationEnabled()
    {
        //todo
    }

    public  void onParameters()
    {
        //todo
    }

    public  void onIsolinesEnabled()
    {
        //todo
    }

    public void onGridEnabled()
    {
        controller.setGridEnabled(!controller.isGridEnabled());
    }
}
