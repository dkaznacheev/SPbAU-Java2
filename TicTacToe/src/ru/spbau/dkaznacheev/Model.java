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
    private BoardState[][] board;

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
    public BoardState[][] getBoard() {
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
        this.board = new BoardState[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                board[i][j] = BoardState.STATE_NONE;
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
    public PlayerType processTurn(int row, int column) {
        if (gameWon) {
            return null;
        }
        if (board[row][column] != BoardState.STATE_NONE) {
            return null;
        }
        PlayerType winner;
        if (gameType == GameType.MULTIPLAYER) {
            if (currentPlayer == PlayerType.PLAYER_X) {
                board[row][column] = BoardState.STATE_X;
                currentPlayer = PlayerType.PLAYER_O;
            } else {
                board[row][column] = BoardState.STATE_O;
                currentPlayer = PlayerType.PLAYER_X;
            }
        } else {
            board[row][column] = BoardState.STATE_X;
            winner = checkWinner();
            if (winner != PlayerType.NOBODY) {
                gameWon = true;
                return winner;
            }
            Point turn = bot.makeTurn(board);
            board[turn.row][turn.column] = BoardState.STATE_O;
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
            if (board[i][0] == board[i][1]
                    && board[i][1] == board[i][2]) {
                if (board[i][0] == BoardState.STATE_X)
                    return PlayerType.PLAYER_X;
                if (board[i][0] == BoardState.STATE_O)
                    return PlayerType.PLAYER_O;
            }
            if (board[0][i] == board[1][i]
                    && board[1][i] == board[2][i]) {
                if (board[0][i] == BoardState.STATE_X)
                    return PlayerType.PLAYER_X;
                if (board[0][i] == BoardState.STATE_O)
                    return PlayerType.PLAYER_O;
            }
        }
        if (board[0][0] == board[1][1]
                && board[1][1] == board[2][2]) {
            if (board[0][0] == BoardState.STATE_X)
                return PlayerType.PLAYER_X;
            if (board[0][0] == BoardState.STATE_O)
                return PlayerType.PLAYER_O;
        }
        if (board[0][2] == board[1][1]
                && board[1][1] == board[2][0]) {
            if (board[0][2] == BoardState.STATE_X)
                return PlayerType.PLAYER_X;
            if (board[0][2] == BoardState.STATE_O)
                return PlayerType.PLAYER_O;
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == BoardState.STATE_NONE) {
                    return PlayerType.NOBODY;
                }
            }
        }

        return PlayerType.DRAW;
    }
}
