package ru.nsu.fit.g16201.migranov.model;

class Cell {
    private boolean isAlive;
    private int impact = 0;
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

    @Override
    public String toString()
    {
        return isAlive ? "1" : "0";
    }

    public int getImpact() {
        return impact;
    }

    public void setImpact(int impact) {
        this.impact = impact;
    }
}
