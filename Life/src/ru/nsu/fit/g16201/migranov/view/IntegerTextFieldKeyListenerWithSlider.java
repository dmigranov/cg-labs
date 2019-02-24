package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class IntegerTextFieldKeyListenerWithSlider extends KeyAdapter {


    private JSlider slider;

    public IntegerTextFieldKeyListenerWithSlider(JSlider slider)
    {
        this.slider = slider;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        char c = e.getKeyChar();
        if(!((c >= '0' && c <= '9') || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE))
        {
            e.consume();
        }
        else
        {
            JTextField field = (JTextField)e.getSource();
            String text = field.getText();
            if(!"".equals(text))
            {
                int value = Integer.parseInt(text);
                if(value >= slider.getMinimum() && value <= slider.getMaximum())
                    slider.setValue(value);
                //else if (value > slider.getMaximum())
                //    slider.setValue(slider.getMaximum());
            }
        }
    }
}
