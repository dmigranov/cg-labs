package ru.nsu.fit.g16201.migranov.model;

public class Field {
    private static double LIVE_BEGIN =  2.0, LIVE_END =  3.3, BIRTH_BEGIN = 2.3, BIRTH_END = 2.9, FST_IMPACT = 1.0, SND_IMPACT = 0.3;

    private int m, n;
    private Cell[][] field;
    public Field (int m, int n)
    {
        this.m = m;
        this.n = n;

        field = new Cell[n][];
        for(int i = 0; i < n; i++)
        {
            if(i % 2 == 0) {
                field[i] = new Cell[m];
                for(int j = 0; j < m; j++)
                    field[i][j] = new Cell();
            }
            else {
                field[i] = new Cell[m - 1];
                for(int j = 0; j < m - 1; j++)
                    field[i][j] = new Cell();
            }
        }

    }

    public void step()
    {
        int firstCount = getFirstCount();
        int secondCound = getSecondCount();

        for(int i = 0; i < n; i++)
        {
            if(i % 2 == 0)
                for(int j = 0; j < m; j++)
                    ;
            else
                for(int j = 0; j < m - 1; j++)
                    ;
            System.out.println();
        }

        printField();
    }

    private int getFirstCount() {
        int count = 0;



        return count;
    }

    private int getSecondCount() {
        int count = 0;



        return count;
    }

    public int getN() {
        return n;
    }


    public int getM() {
        return m;
    }

    //for testing only
    private void printField()
    {
        for(int i = 0; i < n; i++)
        {
            if(i % 2 == 0)
                for(int j = 0; j < m; j++)
                    System.out.print(field[i][j] + " ");
            else
                for(int j = 0; j < m - 1; j++)
                    System.out.print(" " + field[i][j]);
            System.out.println();
        }
    }
}
