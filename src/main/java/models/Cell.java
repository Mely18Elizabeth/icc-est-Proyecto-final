package models;

import java.io.Serializable;

public class Cell implements Serializable {

    private final int row;
    private final int col;
    private CellState state;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.state = CellState.VACIO;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ") - " + state;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Cell)) return false;
        Cell other = (Cell) obj;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}