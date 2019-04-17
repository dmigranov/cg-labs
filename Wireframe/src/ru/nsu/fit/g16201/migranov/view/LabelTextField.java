package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

public class LabelTextField extends JPanel {
    LabelTextField(String s, JTextField textField, KeyListener listener)
    {
        textField.setPreferredSize(new Dimension(60, 30));
        add(new JLabel(s));
        add(textField);
        textField.addKeyListener(listener);
        setAlignmentX(LEFT_ALIGNMENT);

    }

}
