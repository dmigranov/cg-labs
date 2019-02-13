package ru.nsu.fit.g16201.migranov.model;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {

    @org.junit.jupiter.api.Test
    void getCounts() {
        Field field = new Field(6, 6);

        field.setCell(1, 1);
        field.setCell(1, 2);
        field.setCell(2, 1);
        assertEquals(3, field.getFirstCount(2, 2));

        field.setCell(3, 2);
        assertEquals(4, field.getFirstCount(2, 2));

        field.setCell(2, 4);
        assertEquals(2, field.getFirstCount(3, 3));
        assertEquals(1, field.getFirstCount(2, 5));
        assertEquals(1, field.getFirstCount(1, 4));
        assertEquals(2, field.getFirstCount(0, 2));
        assertEquals(1, field.getFirstCount(4, 3));

        field.setCell(4, 4);
        assertEquals(1, field.getFirstCount(5, 4));

        assertEquals(4, field.getSecondCount(3, 2));
        assertEquals(1, field.getSecondCount(0, 3));
        assertEquals(1, field.getSecondCount(2, 0));
        assertEquals(3, field.getSecondCount(2, 4));
        assertEquals(2, field.getSecondCount(5, 2));
    }


    /*@org.junit.jupiter.api.Test
    void step()
    {
        Field field = new Field(30, 30);
        field.step();
    }*/
}