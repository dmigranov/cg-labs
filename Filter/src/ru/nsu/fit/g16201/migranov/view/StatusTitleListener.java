package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StatusTitleListener extends MouseAdapter {
    private JLabel label;
    public StatusTitleListener(JLabel label) {
        this.label = label;
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        JComponent item = (JComponent)e.getSource();
        label.setText(item.getToolTipText());
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        label.setText("");
    }
}
