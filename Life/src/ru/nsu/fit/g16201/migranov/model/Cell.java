package ru.nsu.fit.g16201.migranov.model;

public class Cell {
    private boolean isAlive;
    private double impact = 0;
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

    public double getImpact() {
        return impact;
    }

    public void setImpact(double impact) {
        this.impact = impact;
    }

    public void invert()
    {
        isAlive = !isAlive;
    }
}
