package ru.spbau.dkaznacheev;

import java.util.Random;

import static ru.spbau.dkaznacheev.Model.BOARD_SIZE;

/**
 * Easy bot that makes its turns randomly.
 */
public class EasyBot implements Bot {
    private final Random random = new Random();

    @Override
    public Point makeTurn(Board board) {
        int row, column;
        do {
            row = Math.abs(random.nextInt()) % BOARD_SIZE;
            column = Math.abs(random.nextInt()) % BOARD_SIZE;
        } while (board.get(row, column) != BoardState.STATE_NONE);
        return new Point(row, column);
    }
}
