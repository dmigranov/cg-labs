package ru.nsu.fit.g16201.migranov.controller;


import ru.nsu.fit.g16201.migranov.model.Field;
import ru.nsu.fit.g16201.migranov.view.FieldPanel;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Controller {

    private FieldPanel fieldPanel;
    private Field field;

    //TODO: создать интерфейс типа fieldPanel а то как-то не по ооп
    public Controller(FieldPanel fieldPanel) {
        this.fieldPanel = fieldPanel;
        field = new Field(10, 10);
        fieldPanel.setField(field);
    }

    public void loadFieldFromFile(File file)
    {
        //короче надо чтобы панели параметры поля всегда передавал контроллер! по умолчанию - какие-то стандартные
        //в файле координаты в формате x,y!!!
        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            line = br.readLine();
            line = line.substring(0, line.indexOf('/'));
            String[] substrings = line.split(" "); //todo: better parsing?
            int m = Integer.parseInt(substrings[0]);
            int n = Integer.parseInt(substrings[1]);
            field = new Field(m, n);

            line = br.readLine();
            line = line.substring(0, line.indexOf('/'));
            int w = Integer.parseInt(line);
            line = br.readLine();
            line = line.substring(0, line.indexOf('/'));
            int k = Integer.parseInt(line);
            fieldPanel.setDrawingParameters(w, k);

            line = br.readLine();
            line = line.substring(0, line.indexOf('/'));
            int all = Integer.parseInt(line);

            //в файле клетки в формате xy, а у меня - yx!
            while ((line = br.readLine()) != null)
            {
                substrings = line.split(" ");
                int x = Integer.parseInt(substrings[0]);
                int y = Integer.parseInt(substrings[1]);
                field.setCell(y, x);

                all--;
            }
            //TODO: проверить all и выкинуть exception

            //когда её передать панели?
            fieldPanel.setField(field);
        }
        catch (IOException e)
        {
            //диалог
        }
    }
}
