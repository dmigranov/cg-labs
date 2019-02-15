package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Field;
import ru.nsu.fit.g16201.migranov.view.FieldPanel;

import javax.swing.*;
import java.io.File;

public class Controller {

    JPanel fieldPanel;
    Field field;

    //TODO: создать интерфейс типа fieldPanel а то как-то не по ооп
    public Controller(FieldPanel fieldPanel) {
        this.fieldPanel = fieldPanel;
        this.field = fieldPanel.getField();
    }

    public void loadFieldFromFile(File file)
    {

    }
}
