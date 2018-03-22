package ru.spbau.dkaznacheev;

import javafx.util.Pair;

import java.util.Random;

import static ru.spbau.dkaznacheev.Model.BOARD_SIZE;

/**
 * An implementation of a bot that almost never loses.
 */
public class HardBot implements Bot {

    /**
     * If it is the first turn.
     */
    private boolean firstTurn = true;

    private Random random = new Random();

    /**
     * Finds a point on the board that ends the game.
     * @param board current board
     * @return point that we have to put an O to, null if there is none
     */
    private Point winning(BoardState[][] board) {
        Point point;
        for (int i = 0; i < BOARD_SIZE; i++) {
            point = twoSameAndEmpty(
                    board,
                    new Point(i, 0),
                    new Point(i, 1),
                    new Point(i, 2));
            if (point != null) {
                return point;
            }
            point = twoSameAndEmpty(
                    board,
                    new Point(0, i),
                    new Point(1, i),
                    new Point(2, i));
            if (point != null) {
                return point;
            }
        }
        point = twoSameAndEmpty(
                board,
                new Point(0, 0),
                new Point(1, 1),
                new Point(2, 2));
        if (point != null) {
            return point;
        }
        point = twoSameAndEmpty(
                board,
                new Point(0, 2),
                new Point(1, 1),
                new Point(2, 0));
        return point;
    }

    /**
     * Checks if two of the three points are of the same non-empty type and the third is empty.
     * @param board current board
     * @param point0 first point
     * @param point1 second point
     * @param point2 third point
     * @return empty point, null if conditions are not met
     */
    private Point twoSameAndEmpty(BoardState[][] board, Point point0, Point point1, Point point2) {
        if (       board[point0.row][point0.column] == BoardState.STATE_NONE
                && board[point1.row][point1.column] == board[point2.row][point2.column]
                && board[point1.row][point1.column] != BoardState.STATE_NONE ) {
            return point0;
        }
        if (       board[point1.row][point1.column] == BoardState.STATE_NONE
                && board[point0.row][point0.column] == board[point2.row][point2.column]
                && board[point0.row][point0.column] != BoardState.STATE_NONE ) {
            return point1;
        }
        if (       board[point2.row][point2.column] == BoardState.STATE_NONE
                && board[point1.row][point1.column] == board[point0.row][point0.column]
                && board[point1.row][point1.column] != BoardState.STATE_NONE ) {
            return point2;
        }
        return null;
    }

    @Override
    public Point makeTurn(BoardState[][] board) {
        if (firstTurn) {
            firstTurn = false;
            if (board[1][1] != BoardState.STATE_NONE) {
                return new Point(0, 0);
            } else {
                return new Point(1, 1);
            }
        }
        Point winning = winning(board);
        if (winning != null) {
            return winning;
        } else {
            int row, column;
            do {
                row = Math.abs(random.nextInt()) % BOARD_SIZE;
                column = Math.abs(random.nextInt()) % BOARD_SIZE;
            } while (board[row][column] != BoardState.STATE_NONE);
            return new Point(row, column);
        }
    }
}
