package ru.spbau.dkaznacheev;

/**
 * Bidimensional point on the board.
 */
public class Point {
    /**
     * Row.
     */
    public final int row;

    /**
     * Column.
     */
    public final int column;

    public Point(int row, int column) {
        this.row = row;
        this.column = column;
    }
}
