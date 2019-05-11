package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.controller.Controller;
import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class RaytracingFrame extends MainFrame {
    private JLabel statusLabel = new JLabel("");
    private List<AbstractButton> deactivatedButtons = new ArrayList<>();
    private WireframePanel wireframePanel;
    private JPanel mainPanel;
    private Controller controller;

    public static void main(String[] args) throws Exception {
        new RaytracingFrame();
    }

    private RaytracingFrame() throws Exception {
        super(800, 600, "Untitled | Denis Migranov, 16201");


        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resize(); //todo
            }
        });

        wireframePanel = new WireframePanel();

        mainPanel.add(wireframePanel);
        controller = new Controller(wireframePanel);
        wireframePanel.setPreferredSize(new Dimension(600, 400));
        wireframePanel.setBackgroundColor(Color.WHITE);
        wireframePanel.clear();
        wireframePanel.requestFocusInWindow();

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
        int width = mainPanel.getWidth();
        int height = mainPanel.getHeight();
        /*double sw = controller.getSw(), sh = controller.getSh();

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
        mainPanel.revalidate();*/
    }

    private void addMenus() throws NoSuchMethodException{
        addSubMenu("File", KeyEvent.VK_F);
        addMenuAndToolBarButton("File/Open", "Load a scene file", KeyEvent.VK_O, "upload-1.png", "onOpen", false);
        addMenuAndToolBarButton("File/Load render settings", "Load render settings from file", KeyEvent.VK_R, "upload.png", "onOpenRenderSettings", true);
        addMenuAndToolBarButton("File/Save render settings as", "Save render settings to specified file", KeyEvent.VK_S, "download.png", "onSaveRenderSettings", true);
        addMenuAndToolBarButton("File/Save image", "Save image", KeyEvent.VK_I, "pic.png", "onSaveImage", true);



        addSubMenu("View", KeyEvent.VK_V);
        addMenuAndToolBarButton("View/Init", "Reset camera", KeyEvent.VK_I, "reload.png", "onInit", true);
        addMenuAndToolBarButton("View/Settings", "Rendering settings", KeyEvent.VK_S, "settings.png", "onShowSettings", true);
        ButtonGroup group = new ButtonGroup();
        addRadioButtonMenuAndToolBarButton("View/Select view","Select view by changing camera position", KeyEvent.VK_V, "camera.png", group, "onSelectView", true, true, true);
        addRadioButtonMenuAndToolBarButton("View/Render","Render", KeyEvent.VK_R, "render.png", group, "onRender", false, true, true);
    }

    public void onOpen()
    {
        File file = getOpenFileName("scene", "A scene description file");
        if(file != null) {
            setTitle(file.getName() + " | Denis Migranov, 16201");
            int r = controller.loadFile(file);
            if(r != 0)
            {
                JOptionPane.showMessageDialog(this, "Wrong file format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                for (AbstractButton b : deactivatedButtons)
                {
                    b.setEnabled(true);
                }
                String fileName = file.getName().replaceFirst("[.][^.]+$", "");
                //todo: renderFile

                controller.drawWireFigures();
            }
        }
    }

    public void onOpenRenderSettings()
    {

    }

    public void onSaveRenderSettings()
    {

    }

    public void onInit()
    {
        controller.reinitialize();
    }

    public void onShowSettings()
    {

    }

    public void onSaveImage()
    {

    }

    public void onSelectView()
    {

    }

    public void onRender()
    {

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
                method.invoke(RaytracingFrame.this);
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

    private void addRadioButtonMenuAndToolBarButton(String path, String tooltip, int mnemonic, String icon, ButtonGroup group, String actionMethod, boolean state, boolean isDeactivated, boolean areToolBarButtonsAdded) throws NoSuchMethodException
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
                    method.invoke(RaytracingFrame.this);
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
}
