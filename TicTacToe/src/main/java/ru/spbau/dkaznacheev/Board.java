package ru.spbau.dkaznacheev;

public class Board {
    private BoardState[][] board;

    public Board(int size) {
        this.board = new BoardState[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                board[i][j] = BoardState.STATE_NONE;
    }

    public BoardState get(int row, int column) {
        return board[row][column];
    }

    public void set(int row, int column, BoardState state) {
        board[row][column] = state;
    }
}
