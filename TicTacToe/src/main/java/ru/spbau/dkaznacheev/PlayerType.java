package ru.spbau.dkaznacheev;

/**
 * Enum for player types and game result.
 */
public enum PlayerType {

    /**
     * X player.
     */
    PLAYER_X,

    /**
     * O player.
     */
    PLAYER_O,

    /**
     * Nobody won: the game is not over yet.
     */
    NOBODY,

    /**
     * Game ended in a draw.
     */
    DRAW
}
