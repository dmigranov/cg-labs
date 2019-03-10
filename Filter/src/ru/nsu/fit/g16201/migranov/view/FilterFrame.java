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

        //todo: сказал вообще избавиться от скролла
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

        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void addMenus() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuAndToolBarButton("File/New", "Start from scratch", KeyEvent.VK_N, "reload.png", "onNew");
        addMenuAndToolBarButton("File/Open", "Open a picture file", KeyEvent.VK_O, "upload-1.png", "onOpen");

        addSubMenu("Pixel operations", KeyEvent.VK_P);
        addMenuAndToolBarButton("Pixel operations/Negative", "Invert the image", KeyEvent.VK_N, "reload.png", "onNegative");
        addMenuAndToolBarButton("Pixel operations/Black and White", "Desaturate the image", KeyEvent.VK_B, "reload.png", "onDesaturate");
        addMenuAndToolBarButton("Pixel operations/Gamma", "Gamma correction of the image", KeyEvent.VK_G, "reload.png", "onGamma");

        addSubMenu("Dithering", KeyEvent.VK_D);
        addMenuAndToolBarButton("Dithering/Ordered dithering", "Dithering the image using the ordered dither algorithm", KeyEvent.VK_O, "reload.png", "onOrderedDither");
        addMenuAndToolBarButton("Dithering/Floyd-Steinberg dithering", "Dithering the image using the Floyd-Steinberg dithering algorithm", KeyEvent.VK_F, "reload.png", "onFloydSteinberg");

        addSubMenu("Filters", KeyEvent.VK_F);
        addMenuAndToolBarButton("Filters/Sharpness filter", "Apply sharpness filter", KeyEvent.VK_S, "reload.png", "onSharpness");
        addMenuAndToolBarButton("Filters/Simple blur filter", "Apply simple blur filter", KeyEvent.VK_B, "reload.png", "onSimpleBlur");
        addMenuAndToolBarButton("Filters/Embossing filter", "Apply embossing filter", KeyEvent.VK_E, "reload.png", "onEmboss");
        addMenuAndToolBarButton("Filters/Watercolor filter", "Apply watercolor filter", KeyEvent.VK_W, "reload.png", "onWatercolor");
        addMenuAndToolBarButton("Filters/Sobel filter", "Apply Sobel edge detection filter", KeyEvent.VK_D, "reload.png", "onSobel");
        addMenuAndToolBarButton("Filters/Roberts filter", "Apply Roberts edge detection filter", KeyEvent.VK_R, "reload.png", "onRoberts");

        addSubMenu("Rotation and zoom", KeyEvent.VK_R);
        addMenuAndToolBarButton("Rotation and zoom/Rotate", "Rotate the image", KeyEvent.VK_R, "reload.png", "onRotate");


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
        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));
        JTextField rLevelField = new JTextField(3);
        JTextField gLevelField = new JTextField(3);
        JTextField bLevelField = new JTextField(3);

        rLevelField.addKeyListener(new IntegerTextFieldKeyListener(2));
        gLevelField.addKeyListener(new IntegerTextFieldKeyListener(2));
        bLevelField.addKeyListener(new IntegerTextFieldKeyListener(2));

        parametersPanel.add(new JLabel("Number of levels of red:   "));
        parametersPanel.add(rLevelField);
        parametersPanel.add(new JLabel("Number of levels of green: "));
        parametersPanel.add(gLevelField);
        parametersPanel.add(new JLabel("Number of levels of blue:  "));
        parametersPanel.add(bLevelField);

        if(JOptionPane.showConfirmDialog(this, parametersPanel, "Color levels", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            String rText = rLevelField.getText();
            String gText = gLevelField.getText();
            String bText = bLevelField.getText();

            try
            {
                int rLevel = Integer.parseInt(rText);
                int gLevel = Integer.parseInt(gText);
                int bLevel = Integer.parseInt(bText);
                if(rLevel == 0 || gLevel == 0 || bLevel == 0) //<=1?
                    throw new NumberFormatException();
                controller.doOrderedDithering(rLevel, gLevel, bLevel);
            }
            catch(NumberFormatException e)
            {
                JOptionPane.showMessageDialog(this, "Wrong m or n; no new field was created", "Wrong m or n", JOptionPane.ERROR_MESSAGE);
            }

        }

    }

    public void onFloydSteinberg()
    {
        //todo: на самом деле здесь не нужна связка слайдера и филда


    }

    public void onSobel()
    {
        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));
        SliderTextFieldPanel panel = new SliderTextFieldPanel(1, 500, 100, "Threshold: ");   //todo; подумать сколько!
        parametersPanel.add(panel);

        if(JOptionPane.showConfirmDialog(this, parametersPanel, "Sobel filter threshold", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            int threshold = panel.getValue();
            controller.applySobelFilter(threshold);
        }
    }

    public void onRoberts()
    {
        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));
        SliderTextFieldPanel panel = new SliderTextFieldPanel(1, 300, 100, "Threshold: ");
        parametersPanel.add(panel);

        if(JOptionPane.showConfirmDialog(this, parametersPanel, "Roberts filter threshold", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            int threshold = panel.getValue();
            controller.applyRobertsFilter(threshold);
        }
    }

    public void onSharpness()
    {
        controller.applySharpnessFilter();
    }

    public void onSimpleBlur()
    {
        controller.applySimpleBlur();
    }

    public void onEmboss()
    {
        controller.applyEmbossing();
    }

    public void onWatercolor()
    {
        controller.applyWatercolor();
    }

    public void onGamma()
    {
        JPanel parametersPanel = new JPanel();
        JTextField field = new JTextField("1",5);
        parametersPanel.add(new JLabel("Gamma (0.01-20): "));
        add(Box.createHorizontalStrut(10));
        parametersPanel.add(field);
        field.addKeyListener(new FloatTextFieldKeyListener());

        if(JOptionPane.showConfirmDialog(this, parametersPanel, "Gamma correction", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            String str = field.getText();
            try {
                double gamma = Double.parseDouble(str);
                if(gamma > 20 || gamma < 0.01)
                    throw new NumberFormatException();
                controller.applyGammaCorrection(gamma);
            }
            catch(NumberFormatException e)
            {
                JOptionPane.showMessageDialog(this, "Please type in a valid floating point number from 0.01 to 20", "Wrong number", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onRotate()
    {

    }

}
