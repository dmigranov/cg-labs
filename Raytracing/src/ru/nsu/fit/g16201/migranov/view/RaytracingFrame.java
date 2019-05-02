package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.controller.Controller;
import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class RaytracingFrame extends MainFrame {
    private JLabel statusLabel = new JLabel("");
    private List<AbstractButton> deactivatedButtons = new ArrayList<>();

    private Controller controller;

    public static void main(String[] args) throws Exception {
        new RaytracingFrame();
    }

    private RaytracingFrame() throws Exception {
        super(800, 600, "Untitled | Denis Migranov, 16201");

        controller = new Controller();

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

    private void addMenus() throws NoSuchMethodException{
        addSubMenu("File", KeyEvent.VK_F);
        addMenuAndToolBarButton("File/Open", "Load a scene file", KeyEvent.VK_O, "upload-1.png", "onOpen", false);
        addMenuAndToolBarButton("File/Load render settings", "Load render settings from file", KeyEvent.VK_R, "upload.png", "onOpenRenderSettings", true);
        addMenuAndToolBarButton("File/Save render settings as", "Save render settings to specified file", KeyEvent.VK_S, "download.png", "onSaveRenderSettings", true);

        addSubMenu("View", KeyEvent.VK_V);
        addMenuAndToolBarButton("View/Init", "Reset camera", KeyEvent.VK_I, "reload.png", "onInit", true);
        addMenuAndToolBarButton("View/Settings", "Rendering settings", KeyEvent.VK_I, "settings.png", "onShowSettings", true);


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
            //something...
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

    }

    public void onShowSettings()
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
}
