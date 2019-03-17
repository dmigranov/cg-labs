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
    private ImagePanel modifiableImagePanel = new ImagePanel(), modifiedImagePanel = new ImagePanel(), originalImagePanel = new ImagePanel();
    private JPanel absorptionPanel = new JPanel(), emissionPanel = new JPanel();
    private JLabel statusLabel = new JLabel("");
    private Controller controller;
    public static void main(String[] args) throws Exception {
        new FilterFrame();
    }

    private FilterFrame() throws Exception {
        super(800, 600, "Untitled | Denis Migranov, 16201");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //панельки
        JPanel mainPanel = new JPanel(), imagesPanel = new JPanel(), graphicsPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        imagesPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 10, 10));
        graphicsPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 132, 10));
        imagesPanel.revalidate();
        originalImagePanel.setPreferredSize(new Dimension(350, 350));
        modifiableImagePanel.setPreferredSize(new Dimension(350, 350));
        modifiedImagePanel.setPreferredSize(new Dimension(350, 350));

        emissionPanel.setPreferredSize(new Dimension(350, 200));
        absorptionPanel.setPreferredSize(new Dimension(350, 200));
        emissionPanel.setBorder(BorderFactory.createDashedBorder(Color.gray, 10, 5));
        absorptionPanel.setBorder(BorderFactory.createDashedBorder(Color.gray, 10, 5)); //todo: только снизу и справа!

        emissionPanel.setVisible(false);
        absorptionPanel.setVisible(false);

        JPanel originalBorderPanel = new JPanel(), modifiableBorderPanel = new JPanel(), modifiedBorderPanel = new JPanel();

        originalBorderPanel.setBorder(BorderFactory.createDashedBorder(Color.gray, 10, 5));
        modifiableBorderPanel.setBorder(BorderFactory.createDashedBorder(Color.gray, 10, 5));
        modifiedBorderPanel.setBorder(BorderFactory.createDashedBorder(Color.gray, 10, 5));
        originalBorderPanel.add(originalImagePanel);
        modifiableBorderPanel.add(modifiableImagePanel);
        modifiedBorderPanel.add(modifiedImagePanel);
        originalBorderPanel.setPreferredSize(new Dimension(352, 352));
        modifiableBorderPanel.setPreferredSize(new Dimension(352, 352));
        modifiedBorderPanel.setPreferredSize(new Dimension(352, 352));
        originalBorderPanel.setLayout(new SpringLayout());
        modifiableBorderPanel.setLayout(new SpringLayout());
        modifiedBorderPanel.setLayout(new SpringLayout());
        imagesPanel.add(originalBorderPanel);
        imagesPanel.add(modifiableBorderPanel);
        imagesPanel.add(modifiedBorderPanel);
        graphicsPanel.add(absorptionPanel);
        graphicsPanel.add(emissionPanel);
        mainPanel.add(imagesPanel);
        mainPanel.add(graphicsPanel);
        originalBorderPanel.revalidate();

        imagesPanel.setMaximumSize(new Dimension(1096, 372));
        graphicsPanel.setMaximumSize(new Dimension(1096, 220));
        mainPanel.setMaximumSize(new Dimension(1096, 592));

        controller = new Controller(originalImagePanel, modifiableImagePanel, modifiedImagePanel, absorptionPanel, emissionPanel);

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

        pack();
        //setResizable(false);////
        setLocationRelativeTo(null);

        setVisible(true);
    }


    private void addMenus() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuAndToolBarButton("File/New", "Start from scratch", KeyEvent.VK_N, "reload.png", "onNew");
        addMenuAndToolBarButton("File/Open", "Open a picture file", KeyEvent.VK_O, "upload-1.png", "onOpen");
        addMenuAndToolBarButton("File/Save as", "Save modified picture as", KeyEvent.VK_S, "download.png", "onSave");
        addMenuAndToolBarButton("File/C to B", "Move the modified image to the second zone", KeyEvent.VK_M, "logout.png", "onMove");
        addMenuAndToolBarButton("File/Exit", "Exit the aplication", KeyEvent.VK_E, "logout.png", "onExit");

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
        addMenuAndToolBarButton("Rotation and zoom/Zoom X2", "Double the image", KeyEvent.VK_Z, "reload.png", "onZoom");

        addSubMenu("Volume rendering", KeyEvent.VK_V);
        addMenuAndToolBarButton("Volume rendering/Open configuration", "Open configuration file", KeyEvent.VK_O, "reload.png", "onOpenConfiguration");

        addSubMenu("Help", KeyEvent.VK_H);
        addMenuAndToolBarButton("Help/About", "About the program", KeyEvent.VK_A, "book.png", "onAbout");

    }

    private void addMenuAndToolBarButton(String path, String tooltip, int mnemonic, String icon, String actionMethod) throws NoSuchMethodException
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

    private File getOpenFileName()
    {
        return FileUtils.getOpenFileName(this);
    }

    public void onNew()
    {
        //todo
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

        JPanel parametersPanel = new JPanel(), radioButtonPanel = new JPanel();
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));
        DitheringParametersPanel ditheringParametersPanel = new DitheringParametersPanel();
        parametersPanel.add(ditheringParametersPanel);
        parametersPanel.add(radioButtonPanel);
        JRadioButton b2 = new JRadioButton("2x2"), b4 = new JRadioButton("4x4", true), b8 = new JRadioButton("8x8");
        ButtonGroup group = new ButtonGroup();

        group.add(b2);
        group.add(b4);
        group.add(b8);
        radioButtonPanel.add(b2);
        radioButtonPanel.add(b4);
        radioButtonPanel.add(b8);

        if(JOptionPane.showConfirmDialog(this, parametersPanel, "Ordered dithering algorithm color levels", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            String rText = ditheringParametersPanel.getRText();
            String gText = ditheringParametersPanel.getGText();
            String bText = ditheringParametersPanel.getBText();
            try
            {
                int rLevel = Integer.parseInt(rText);
                int gLevel = Integer.parseInt(gText);
                int bLevel = Integer.parseInt(bText);
                if(rLevel == 0 || gLevel == 0 || bLevel == 0) //<=1?
                    throw new NumberFormatException();
                int matrixSize;
                if(b2.isSelected())
                    matrixSize = 2;
                else if(b4.isSelected())
                    matrixSize = 4;
                else
                    matrixSize = 8;
                controller.doOrderedDithering(rLevel, gLevel, bLevel, matrixSize);
            }
            catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Wrong threshold values", "Wrong input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onFloydSteinberg()
    {
        DitheringParametersPanel ditheringParametersPanel = new DitheringParametersPanel();

        if(JOptionPane.showConfirmDialog(this, ditheringParametersPanel, "Floyd-Steinberg algorithm color levels", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            String rText = ditheringParametersPanel.getRText();
            String gText = ditheringParametersPanel.getGText();
            String bText = ditheringParametersPanel.getBText();
            try
            {
                int rLevel = Integer.parseInt(rText);
                int gLevel = Integer.parseInt(gText);
                int bLevel = Integer.parseInt(bText);
                if(rLevel == 0 || gLevel == 0 || bLevel == 0) //<=1?
                    throw new NumberFormatException();
                controller.doFloydSteinbergDithering(rLevel, gLevel, bLevel);
            }
            catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Wrong threshold values", "Wrong input", JOptionPane.ERROR_MESSAGE);
            }
        }
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
        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));
        NegativeSliderTextFieldPanel panel = new NegativeSliderTextFieldPanel(-180, 180, 0, "Angle: ");
        parametersPanel.add(panel);

        if(JOptionPane.showConfirmDialog(this, parametersPanel, "Sobel filter threshold", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            int angle = panel.getValue();
            controller.rotate(angle);
        }
    }

    public void onZoom()
    {
        controller.zoom();
    }

    private class DitheringParametersPanel extends JPanel
    {
        JTextField rLevelField = new JTextField("2", 3);
        JTextField gLevelField = new JTextField("2", 3);
        JTextField bLevelField = new JTextField("2", 3);

        DitheringParametersPanel()
        {
            super();
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            rLevelField.addKeyListener(new IntegerTextFieldKeyListener(2));
            gLevelField.addKeyListener(new IntegerTextFieldKeyListener(2));
            bLevelField.addKeyListener(new IntegerTextFieldKeyListener(2));
            add(new JLabel("Number of levels of red:   "));
            add(rLevelField);
            add(new JLabel("Number of levels of green: "));
            add(gLevelField);
            add(new JLabel("Number of levels of blue:  "));
            add(bLevelField);
        }

        String getRText()
        {
            return rLevelField.getText();
        }
        String getGText()
        {
            return gLevelField.getText();
        }
        String getBText()
        {
            return bLevelField.getText();
        }
    }

    public void onOpenConfiguration()
    {
        File file = getOpenFileName("txt", "A volume rendering description file");
        if(file != null) {
            controller.openConfigurationFile(file);
        }
    }

    public void onAbout()
    {
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
        aboutPanel.add(new JLabel("Made as a part of NSU Computer Graphics course"));
        aboutPanel.add(new JLabel("Denis Migranov, group 16201, 2019"));
        aboutPanel.add(new JLabel("Icons used are from www.flaticon.com/packs/multimedia-collection"));
        JOptionPane.showMessageDialog(this, aboutPanel, "About FIT_16201_Migranov_Filter", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onSave() {
        File file = getSaveFileName("png", "A PNG file");
        if (file != null) {
            controller.saveImageToFile(file);
        }
    }

    public void onMove()
    {
        controller.moveModifiedToModifiable();
    }

    public void onExit()
    {
        System.exit(0);
    }
}
