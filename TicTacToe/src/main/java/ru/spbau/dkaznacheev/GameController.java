package ru.spbau.dkaznacheev;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import static ru.spbau.dkaznacheev.Log.logGame;
import static ru.spbau.dkaznacheev.Model.BOARD_SIZE;

/**
 * Game controller class.
 */
public class GameController {

    /**
     * Game logic model object.
     */
    private Model model;

    /**
     * Type of the game.
     */
    private GameType gameType;

    @FXML
    private Text label;
    @FXML
    private Button button00;
    @FXML
    private Button button01;
    @FXML
    private Button button02;
    @FXML
    private Button button10;
    @FXML
    private Button button11;
    @FXML
    private Button button12;
    @FXML
    private Button button20;
    @FXML
    private Button button21;
    @FXML
    private Button button22;

    private Button[][] buttons;

    public GameController(GameType gameType) {
        this.gameType = gameType;
    }

    @FXML
    public void initialize() {
        model = new Model(gameType);
        buttons = new Button[BOARD_SIZE][BOARD_SIZE];
        buttons[0][0] = button00;
        buttons[0][1] = button01;
        buttons[0][2] = button02;
        buttons[1][0] = button10;
        buttons[1][1] = button11;
        buttons[1][2] = button12;
        buttons[2][0] = button20;
        buttons[2][1] = button21;
        buttons[2][2] = button22;

    }

    /**
     * Processes a click on a button.
     * @param row row of buttons
     * @param column column of buttons
     */
    public void processTurn(int row, int column) {
        PlayerType winner = model.makeTurn(row, column);
        if (winner == null)
            return;
        if (winner != PlayerType.NOBODY) {
            logGame(winner, gameType);
        }
        if (winner == PlayerType.PLAYER_X) {
            label.setText("X won!");
        } else if (winner == PlayerType.PLAYER_O) {
            label.setText("O won!");
        } else if (winner == PlayerType.DRAW) {
            label.setText("Draw!");
        } else if (model.getCurrentPlayer() == PlayerType.PLAYER_X) {
            label.setText("X turn");
        } else if (model.getCurrentPlayer() == PlayerType.PLAYER_O) {
            label.setText("O turn");
        }
        updateBoard();
    }

    /**
     * Updates the board buttons.
     */
    private void updateBoard() {
        Board board = model.getBoard();
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board.get(i, j) == BoardState.STATE_X) {
                    buttons[i][j].setText("X");
                } else if (board.get(i, j) == BoardState.STATE_O) {
                    buttons[i][j].setText("O");
                } else {
                    buttons[i][j].setText("");
                }
            }
    }

    @FXML
    public void handle00Action(ActionEvent actionEvent) {
        processTurn(0, 0);
    }

    @FXML
    public void handle01Action(ActionEvent actionEvent) {
        processTurn(0, 1);
    }

    @FXML
    public void handle02Action(ActionEvent actionEvent) {
        processTurn(0, 2);
    }

    @FXML
    public void handle10Action(ActionEvent actionEvent) {
        processTurn(1, 0);
    }

    @FXML
    public void handle11Action(ActionEvent actionEvent) {
        processTurn(1, 1);
    }

    @FXML
    public void handle12Action(ActionEvent actionEvent) {
        processTurn(1, 2);
    }

    @FXML
    public void handle20Action(ActionEvent actionEvent) {
        processTurn(2, 0);
    }

    @FXML
    public void handle21Action(ActionEvent actionEvent) {
        processTurn(2, 1);
    }

    @FXML
    public void handle22Action(ActionEvent actionEvent) {
        processTurn(2, 2);
    }

    @FXML
    public void handleNewGameAction(ActionEvent actionEvent) {
        model.reset();
        updateBoard();
    }

    @FXML
    public void handleBackAction(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("start.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root, 650, 650));
        } catch (IOException e) {
            System.err.println("IO error!");
        }
    }
}
