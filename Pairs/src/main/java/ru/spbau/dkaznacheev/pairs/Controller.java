package ru.spbau.dkaznacheev.pairs;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Controller {

    /**
     * Game model for the game.
     */
    private Game game;

    /**
     * Whether a game is waiting now.
     */
    private boolean sleeping = false;

    /**
     * Currently selected button.
     */
    private PairButton selectedButton;

    /**
     * Number of pairs already found.
     */
    private int foundPairs;

    /**
     * Size of the field.
     */
    private @Nullable Integer fieldSize;

    @FXML
    private GridPane board;

    @FXML
    public void initialize() {
        fieldSize = getFieldSizeDialog();
        if (fieldSize == null) {
            Platform.exit();
            return;
        }
        constructField(fieldSize);
        selectedButton = null;
        sleeping = false;
        foundPairs = 0;
        game = new Game(fieldSize);
    }

    /**
     * Processes a click on a button.
     * @param button a clicked button.
     */
    private void processClick(PairButton button) {
        board.requestFocus();
        if (sleeping) {
            return;
        }
        button.setText(Integer.toString(button.number));
        if (selectedButton == null) {
            selectedButton = button;
            button.setDisable(true);
        } else  {
            button.setDisable(true);
            if (selectedButton.number == button.number) {
                game.addPair();
                if (game.isWon()) {
                    showGameWon();
                    Platform.exit();
                }
            } else {
                sleepThenEnableButtons(750, button, selectedButton);
            }
            selectedButton = null;
        }
    }

    /**
     * Constructs a field of given size.
     * @param size size of field
     */
    private void constructField(int size) {
        List<Integer> values = Util.generateRandomList(size);
        Iterator<Integer> iterator = values.iterator();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                PairButton button = new PairButton(iterator.next());
                button.setMinHeight(50);
                button.setMinWidth(50);
                button.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
                button.setOnAction((v)-> {
                   processClick(button);
                });
                board.add(button, i, j);
            }
        }
    }

    /**
     * Show an alert that the game is over.
     */
    private void showGameWon() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("You win!");
        alert.setHeaderText(null);
        alert.setContentText("Congratulations!");
        alert.showAndWait();
    }

    /**
     * Sleeps for given amount of ms then enables two buttons.
     * @param ms time to sleep
     * @param button1 first button
     * @param button2 second button
     */
    private void sleepThenEnableButtons(int ms, Button button1, Button button2) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    sleeping = true;
                    Thread.sleep(ms);
                } catch (InterruptedException e) {
                    sleeping = false;
                }
                return null;
            }
        };
        sleeper.setOnSucceeded((h) -> {
            sleeping = false;
            button1.setDisable(false);
            button1.setText("");
            button2.setDisable(false);
            button2.setText("");
        });
        new Thread(sleeper).start();
    }

    /**
     * Retrieve a field size from user.
     * @return field size
     */
    private @Nullable Integer getFieldSizeDialog() {
        TextInputDialog dialog = new TextInputDialog("4");
        dialog.setTitle("");
        dialog.setHeaderText(null);
        dialog.setContentText("Select field size:");
        while (true) {
            Optional<String> result = dialog.showAndWait();
            try {
                if (!result.isPresent()) {
                    return null;
                }
                Integer num = Integer.parseInt(result.get());
                if (num <= 0) {
                    showAndWaitForError("Field too small, try again!");
                    continue;
                }
                if (num > 16) {
                    showAndWaitForError("Field too large, try again!");
                    continue;
                }
                if (num % 2 == 1) {
                    showAndWaitForError("Odd number, try again!");
                    continue;
                }
                return num;
            } catch (NumberFormatException e) {
                showAndWaitForError("Not a number, try again!");
            }
        }

    }

    /**
     * Show an error message and wait for user to close it.
     * @param message message to show
     */
    private void showAndWaitForError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
