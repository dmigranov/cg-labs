package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.event.KeyListener;

public class LabelTextField extends JPanel {
    LabelTextField(String s, JTextField textField, KeyListener listener)
    {
        add(new JLabel(s));
        add(textField);
        addKeyListener(listener);
    }

}
