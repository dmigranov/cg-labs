package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class IntegerTextFieldKeyListener extends KeyAdapter {
    int characterCount;
    IntegerTextFieldKeyListener(int characterCount)
    {
        this.characterCount = characterCount;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
        char c = e.getKeyChar();
        if(!((c >= '0' && c <= '9') || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) || ((JTextField)e.getSource()).getText().length() >= characterCount)
        {
            e.consume();
        }
    }
}
