package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RaytracingFrame extends MainFrame {
    private JLabel statusLabel = new JLabel("");
    private List<AbstractButton> deactivatedButtons = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new RaytracingFrame();
    }

    private RaytracingFrame() throws Exception {


    }
}
