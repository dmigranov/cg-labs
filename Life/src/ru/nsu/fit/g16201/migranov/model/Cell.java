package ru.nsu.fit.g16201.migranov.model;

class Cell {
    private boolean isAlive;

    public void set()
    {
        isAlive = true;
    }
    public void clear()
    {
        isAlive = false;
    }

    public Cell()
    {
        isAlive = false;
    }

    public Cell(boolean isAlive)
    {
        this.isAlive = isAlive;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
