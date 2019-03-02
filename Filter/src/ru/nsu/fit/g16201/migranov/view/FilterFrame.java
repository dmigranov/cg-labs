package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class FilterFrame extends MainFrame {
    JPanel originalImagePanel = new JPanel(), modifiableImagePanel = new JPanel(), modifiedImagePanel = new JPanel();

    public static void main(String[] args) throws Exception {
        new FilterFrame();
    }

    private FilterFrame() throws Exception {
        super(800, 600, "Untitled | Denis Migranov, 16201");
        JLabel statusLabel = new JLabel("");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     //насколько понимаю, спрашивать не нужно ничего

        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        mainPanel.setPreferredSize(new Dimension(1200, 500));  //расчитать
        originalImagePanel.setPreferredSize(new Dimension(350, 350));
        modifiableImagePanel.setPreferredSize(new Dimension(350, 350));
        modifiedImagePanel.setPreferredSize(new Dimension(350, 350));
        originalImagePanel.setBorder(BorderFactory.createDashedBorder(Color.gray, 10, 5));
        mainPanel.add(originalImagePanel);
        mainPanel.add(modifiableImagePanel);
        mainPanel.add(modifiedImagePanel);


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
}
