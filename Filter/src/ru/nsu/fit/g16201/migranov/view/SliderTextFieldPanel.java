package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderTextFieldPanel extends JPanel {
    public SliderTextFieldPanel(int min, int max, int startValue, String text)
    {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, startValue);
        JTextField field = new JTextField(startValue + "",2);

        field.addKeyListener(new IntegerTextFieldKeyListenerWithSlider(slider));
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                String fieldText = field.getText();

                if(!"".equals(fieldText)) {
                    int fieldValue = Integer.parseInt(fieldText);
                    if (fieldValue >= min && fieldValue <= max) {
                        field.setText(value + "");
                    }
                }
                else {
                    field.setText(value + "");
                }
            }
        });

        add(new JLabel(text));
        add(field);
        add(Box.createHorizontalStrut(10));
        add(slider);
    }
}
