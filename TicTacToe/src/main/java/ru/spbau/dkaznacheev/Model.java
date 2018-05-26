package ru.spbau.dkaznacheev;

/**
 * Game logic model.
 */
public class Model {

    /**
     * Size of the board.
     */
    public static final int BOARD_SIZE = 3;

    /**
     * Game board.
     */
    private Board board;

    /**
     * Type of the game.
     */
    private final GameType gameType;

    /**
     * A bot that plays the game.
     */
    private Bot bot;

    /**
     * Whether the game has ended.
     */
    private boolean gameWon;

    /**
     * Returns current player.
     * @return current player
     */
    public PlayerType getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Current player.
     */
    private PlayerType currentPlayer = PlayerType.PLAYER_X;

    /**
     * Returns board.
     * @return board
     */
    public Board getBoard() {
        return board;
    }

    public Model(GameType gameType) {
        this.gameType = gameType;
        reset();
    }

    /**
     * Resets the game.
     */
    public void reset() {
        currentPlayer = PlayerType.PLAYER_X;
        gameWon = false;
        this.board = new Board(BOARD_SIZE);
        
        if (gameType == GameType.BOT_EASY) {
            bot = new EasyBot();
        }
        if (gameType == GameType.BOT_HARD) {
            bot = new HardBot();
        }
    }

    /**
     * Processes a turn.
     * @param row row of a turn
     * @param column column of a turn
     * @return winning player
     */
    public PlayerType makeTurn(int row, int column) {
        if (gameWon) {
            return null;
        }
        if (board.get(row, column) != BoardState.STATE_NONE) {
            return null;
        }
        PlayerType winner;
        if (gameType == GameType.MULTIPLAYER) {
            if (currentPlayer == PlayerType.PLAYER_X) {
                board.set(row, column, BoardState.STATE_X);
                currentPlayer = PlayerType.PLAYER_O;
            } else {
                board.set(row, column, BoardState.STATE_O);
                currentPlayer = PlayerType.PLAYER_X;
            }
        } else {
            board.set(row, column, BoardState.STATE_X);
            winner = checkWinner();
            if (winner != PlayerType.NOBODY) {
                gameWon = true;
                return winner;
            }
            Point turn = bot.makeTurn(board);
            board.set(turn.row, turn.column, BoardState.STATE_O);
        }
        winner = checkWinner();
        if (winner != PlayerType.NOBODY) {
            gameWon = true;
        }
        return winner;
    }

    /**
     * Checks who won.
     * @return winning player
     */
    private PlayerType checkWinner() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board.get(i, 0) == board.get(i, 1)
                    && board.get(i, 1) == board.get(i, 2)) {
                if (board.get(i, 0) == BoardState.STATE_X)
                    return PlayerType.PLAYER_X;
                if (board.get(i, 0) == BoardState.STATE_O)
                    return PlayerType.PLAYER_O;
            }
            if (board.get(0, i) == board.get(1, i)
                    && board.get(1, i) == board.get(2, i)) {
                if (board.get(0, i) == BoardState.STATE_X)
                    return PlayerType.PLAYER_X;
                if (board.get(0, i) == BoardState.STATE_O)
                    return PlayerType.PLAYER_O;
            }
        }
        if (board.get(0, 0) == board.get(1, 1)
                && board.get(1, 1) == board.get(2, 2)) {
            if (board.get(0, 0) == BoardState.STATE_X)
                return PlayerType.PLAYER_X;
            if (board.get(0, 0) == BoardState.STATE_O)
                return PlayerType.PLAYER_O;
        }
        if (board.get(0, 2) == board.get(1, 1)
                && board.get(1, 1) == board.get(2, 0)) {
            if (board.get(0, 2) == BoardState.STATE_X)
                return PlayerType.PLAYER_X;
            if (board.get(0, 2) == BoardState.STATE_O)
                return PlayerType.PLAYER_O;
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board.get(i, j) == BoardState.STATE_NONE) {
                    return PlayerType.NOBODY;
                }
            }
        }

        return PlayerType.DRAW;
    }
}
