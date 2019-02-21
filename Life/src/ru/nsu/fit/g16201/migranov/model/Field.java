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
        //todo: перерасчёт импактов нужно будет вынести туда, где будет изменение стейта клетки (для динамического изменения как в примерах). Тогда не надо будет обсчитывать все клетки
        //в таком случае, после изменения состояния (т.е после степа) нужно будет пересчитать импакты как тут

        for(int y = 0; y < n; y++)
        {
            for(int x = 0; x < (y % 2 == 0 ? m : m-1); x++)
            {
                int firstCount = getFirstCount(y,x);
                int secondCount = getSecondCount(y,x);
                double impact = FST_IMPACT * firstCount + SND_IMPACT * secondCount;
                field[y][x].setImpact(impact);
            }
        }

        //может, для импактов лучше создавать отдельный массив?..
        //пересчёт состояний
        for(int y = 0; y < n; y++)
        {
            for (int x = 0; x < (y % 2 == 0 ? m : m - 1); x++)
            {
                Cell cell = field[y][x];
                double impact = cell.getImpact();
                if(!cell.isAlive() && (BIRTH_BEGIN <= impact && impact <= BIRTH_END))
                    cell.set();
                if (cell.isAlive() && !(LIVE_BEGIN <= impact && impact <= LIVE_END))
                    cell.clear();

            }
        }

        //printField();
    }

    //пересчитывает импакты вокруг точки с кооррдинатами y x - нужно при изменении состояния
    private void recalculateImpacts(int y, int x)
    {
        boolean state = field[y][x].isAlive();      //это то, что сейчас. а то что было - противоположное этоиу
    }

    protected int getFirstCount(int y, int x) {
        int count = 0;

        //прошлый вариант (без exists) был более оптимален, но этот проще?
        if(exists(y, x - 1) && field[y][x-1].isAlive())
            count++;
        //if(x < (y % 2 == 0 ? m : m-1) - 1 && field[y][x+1].isAlive())
        if(exists(y, x+1) && field[y][x+1].isAlive())
            count++;
        if(exists(y - 1, x) && field[y-1][x].isAlive())
            count++;
        if(exists(y+1, x) && field[y+1][x].isAlive())
            count++;
        if(y % 2 == 0)
        {
            if(exists(y-1, x-1) && field[y-1][x-1].isAlive())
                count++;
            if(exists(y+1,x-1) && field[y+1][x-1].isAlive())
                count++;
        }
        else if (y % 2 == 1)
        {
            if(exists(y-1, x+1) && field[y-1][x+1].isAlive())
                count++;
            if(exists(y+1, x+1) && field[y+1][x+1].isAlive())
                count++;
        }

        return count;
    }

    protected int getSecondCount(int y, int x) {
        int count = 0;

        if(exists(y - 2, x) && field[y-2][x].isAlive())
            count++;
        if(exists(y + 2, x) && field[y+2][x].isAlive())
            count++;

        if(y % 2 == 0)
        {
            if(exists(y + 1, x - 2) && field[y + 1][x - 2].isAlive())
                count++;
            if(exists(y - 1, x - 2) && field[y - 1][x - 2].isAlive())
                count++;
            if(exists(y + 1, x + 1) && field[y + 1][x + 1].isAlive())
                count++;
            if(exists(y - 1, x + 1) && field[y - 1][x + 1].isAlive())
                count++;
        }
        else
        {
            if(exists(y + 1, x + 2) && field[y + 1][x + 2].isAlive())
                count++;
            if(exists(y - 1, x + 2) && field[y - 1][x + 2].isAlive())
                count++;
            if(exists(y + 1, x - 1) && field[y + 1][x - 1].isAlive())
                count++;
            if(exists(y - 1, x - 1) && field[y - 1][x - 1].isAlive())
                count++;
        }

        return count;
    }

    public int getN() {
        return n;
    }


    public int getM() {
        return m;
    }

    //for testing purposes only
    public void printField()
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
        System.out.println();
    }

    protected void printImpactField()
    {
        for(int i = 0; i < n; i++)
        {
            if(i % 2 == 0)
                for(int j = 0; j < m; j++)
                    System.out.print(field[i][j].getImpact() + " ");
            else
                for(int j = 0; j < m - 1; j++)
                    System.out.print(" " + field[i][j].getImpact());
            System.out.println();
        }
    }

    public void setCell(int y, int x)
    {
        field[y][x].set();
    }
    public void clearCell(int y, int x)
    {
        field[y][x].clear();
    }
    public void invertrCell(int y, int x)
    {
        field[y][x].invert();
    }

    private boolean exists(int y, int x)
    {
        if(y < 0 || x < 0 || y >= n || x >= (y % 2 == 0 ? m : m-1))
            return false;
        return true;
    }

    public boolean isAlive(int y, int x) {
        return field[y][x].isAlive();
    }
}
