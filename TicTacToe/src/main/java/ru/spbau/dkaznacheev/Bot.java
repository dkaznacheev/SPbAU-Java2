package ru.spbau.dkaznacheev;

/**
 * A bot interface capable of making turns.
 * Currently bot only plays as O.
 */
public interface Bot {
    /**
     * Make a turn on a board.
     * @param board current board
     * @return point where the turn is made
     */
    Point makeTurn(Board board);
}
