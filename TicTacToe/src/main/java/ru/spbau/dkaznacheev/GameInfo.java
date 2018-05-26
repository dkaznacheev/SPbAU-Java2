package ru.spbau.dkaznacheev;

/**
 * An info storage for the logger.
 */
public class GameInfo {

    /**
     * Type of game.
     */
    public final GameType gameType;

    /**
     * Who won the game.
     */
    public final PlayerType result;

    public GameInfo(GameType gameType, PlayerType result) {
        this.gameType = gameType;
        this.result = result;
    }
}
