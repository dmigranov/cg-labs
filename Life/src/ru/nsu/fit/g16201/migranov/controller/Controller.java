package ru.nsu.fit.g16201.migranov.controller;


import ru.nsu.fit.g16201.migranov.model.Field;
import ru.nsu.fit.g16201.migranov.view.FieldPanel;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Controller {

    FieldPanel fieldPanel;
    Field field;

    //TODO: создать интерфейс типа fieldPanel а то как-то не по ооп
    public Controller(FieldPanel fieldPanel) {
        this.fieldPanel = fieldPanel;
        field = new Field(10, 10);
        fieldPanel.setField(field);
    }

    public void loadFieldFromFile(File file)
    {
        //TODO: продумать, как Frame, Panel и Controller и их контроллеры связаны друг с другом
        //короче надо чтобы панели параметры поля всегда передавал контроллер! по умолчанию - какие-то стандартные

        //в файле координаты в формате x,y!
        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            line = br.readLine();
            line = line.substring(0, line.indexOf('/'));
            String[] substrs = line.split(" "); //todo
            int m = Integer.parseInt(substrs[0]);
            int n = Integer.parseInt(substrs[1]);

            field = new Field(m, n);



            while ((line = br.readLine()) != null) {

            }

            //когда её передать панели?
            fieldPanel.setField(field);
        }
        catch (IOException e)
        {
            //диалог
        }
    }
}
