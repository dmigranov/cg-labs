package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SliderTextFieldPanel extends JPanel {
    private boolean sliderSetByTextField = false;        //надо чтобы всё красиво работало в случаях, когда введённые значения выходят за пределы [min, max]
    private JSlider slider;
    public SliderTextFieldPanel(int min, int max, int startValue, String text)
    {
        slider = new JSlider(JSlider.HORIZONTAL, min, max, startValue);
        JTextField field = new JTextField(startValue + "",2);

        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if(!((c >= '0' && c <= '9') || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE))
                {
                    e.consume();
                }
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
                        else if (value > slider.getMaximum()) {
                            sliderSetByTextField = true;
                            slider.setValue(slider.getMaximum());
                        }
                        else {
                            sliderSetByTextField = true;
                            slider.setValue(slider.getMinimum());
                        }

                    }
                }
            }
        });
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                String fieldText = field.getText();

                if(!sliderSetByTextField)
                    field.setText(value + "");

                sliderSetByTextField = false;
            }
        });

        add(new JLabel(text));
        add(field);
        add(Box.createHorizontalStrut(10));
        add(slider);
    }

    public int getValue()
    {
        return slider.getValue();
    }
}
