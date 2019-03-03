package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.controller.Controller;
import ru.nsu.fit.g16201.migranov.view.frametemplate.FileUtils;
import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;

public class FilterFrame extends MainFrame {
    private ImagePanel originalImagePanel = new ImagePanel(), modifiableImagePanel = new ImagePanel(), modifiedImagePanel = new ImagePanel();
    private JLabel statusLabel = new JLabel("");
    private Controller controller;
    public static void main(String[] args) throws Exception {
        new FilterFrame();
    }

    private FilterFrame() throws Exception {
        super(800, 600, "Untitled | Denis Migranov, 16201");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     //насколько понимаю, спрашивать не нужно ничего

        //панельки
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 10, 10));
        mainPanel.revalidate();
        originalImagePanel.setBorder(BorderFactory.createDashedBorder(Color.gray, 10, 5));
        modifiableImagePanel.setBorder(BorderFactory.createDashedBorder(Color.gray, 10, 5));
        modifiedImagePanel.setBorder(BorderFactory.createDashedBorder(Color.gray, 10, 5));
        originalImagePanel.setPreferredSize(new Dimension(352, 352));   //так? и тогда в рисовании смещаемся на 1. это чтобы учесть границы
        modifiableImagePanel.setPreferredSize(new Dimension(352, 352));
        modifiedImagePanel.setPreferredSize(new Dimension(352, 352));
        mainPanel.add(originalImagePanel);
        mainPanel.add(modifiableImagePanel);
        mainPanel.add(modifiedImagePanel);
        controller = new Controller(originalImagePanel, modifiableImagePanel, modifiedImagePanel);

        addMenus();

        JScrollPane scrollPane = new JScrollPane(mainPanel);
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

        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void addMenus() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuAndToolBarButton("File/New", "Start from scratch", KeyEvent.VK_N, "reload.png", "onNew");
        addMenuAndToolBarButton("File/Open", "Open a picture file", KeyEvent.VK_O, "upload-1.png", "onOpen");

        addSubMenu("Edit", KeyEvent.VK_F);
        addMenuAndToolBarButton("Edit/Negative", "Invert the image", KeyEvent.VK_N, "reload.png", "onNegative");
        addMenuAndToolBarButton("Edit/Black and White", "Desaturate the image", KeyEvent.VK_B, "reload.png", "onDesaturate");
        addMenuAndToolBarButton("Edit/Ordered dithering", "Dithering the image using the ordered dither algorithm", KeyEvent.VK_O, "reload.png", "onOrderedDither");
        addMenuAndToolBarButton("Edit/Floyd-Steinberg dithering", "Dithering the image using the Floyd-Steinberg dithering algorithm", KeyEvent.VK_F, "reload.png", "onFloydSteinberg");


    }

    private void addMenuAndToolBarButton(String path, String tooltip, int mnemonic, String icon, String actionMethod) throws NoSuchMethodException
    {
        //todo: нужны ли менюшкам иконки? подумоть...
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
                method.invoke(FilterFrame.this);
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
    }

    public File getOpenFileName()
    {
        return FileUtils.getOpenFileName(this);
    }

    public void onNew()
    {

    }

    public void onOpen()
    {
        File file = getOpenFileName();
        if(file != null) {
            setTitle(file.getName() + " | Denis Migranov, 16201");
            controller.openImage(file);
        }
    }

    public void onNegative()
    {
        controller.invert();
    }

    public void onDesaturate()
    {
        controller.desaturate();
    }

    public void onOrderedDither()
    {
        controller.doOrderedDithering();
    }

    public void onFloydSteinberg()
    {
        //todo: на самом деле здесь не нужна связка слайдера и филда
        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));
        SliderTextFieldPanel panel = new SliderTextFieldPanel(15, 25, 20, "Value: ");
        parametersPanel.add(panel);

        JOptionPane.showConfirmDialog(this, parametersPanel, "Field and visualisation parameters", JOptionPane.OK_CANCEL_OPTION);

    }
}
