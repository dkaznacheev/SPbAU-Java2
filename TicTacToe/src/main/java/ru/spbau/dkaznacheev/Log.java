package ru.spbau.dkaznacheev;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Static class for logging.
 */
public class Log {

    /**
     * A log where GameInfos are stored.
     */
    private static List<GameInfo> log = new LinkedList<>();

    /**
     * Template string for formatting.
     */
    private static final String template = "%TYPE% ended with %RESULT%";

    /**
     * Adds a game to the log.
     * @param result result of the game
     * @param gameType type of the game
     */
    public static void logGame(PlayerType result, GameType gameType) {
        log.add(new GameInfo(gameType, result));
    }

    /**
     * Pretty prints the log.
     * @return log as a string
     */
    public static String getLog() {
        if (log.isEmpty()) {
            return "No games found";
        }
        StringJoiner joiner = new StringJoiner("\n");
        String gameTypeString = "";
        String resultString = "";
        for (GameInfo info : log) {
            if (info.gameType == GameType.MULTIPLAYER) {
                gameTypeString = "Multiplayer game";
            } else if (info.gameType == GameType.BOT_EASY) {
                gameTypeString = "Game versus easy bot";
            } else {
                gameTypeString = "Game versus hard bot";
            }

            if (info.result == PlayerType.DRAW) {
                resultString = "a draw";
            } else if (info.result == PlayerType.PLAYER_X) {
                resultString = "X victory";
            } else {
                resultString = "O victory";
            }
            joiner.add(
                    template.replace("%TYPE%", gameTypeString)
                            .replace("%RESULT%", resultString));
        }
        return joiner.toString();
    }

}
