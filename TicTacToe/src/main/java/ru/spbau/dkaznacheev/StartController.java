package ru.spbau.dkaznacheev;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

import static ru.spbau.dkaznacheev.Log.getLog;

public class StartController {
    @FXML
    private void handleMultiplayerButtonAction(ActionEvent actionEvent) {
        launchGame(GameType.MULTIPLAYER, actionEvent);
    }

    @FXML
    public void handleHardBotButtonAction(ActionEvent actionEvent) {
        launchGame(GameType.BOT_HARD, actionEvent);
    }

    @FXML
    public void handleEasyBotButtonAction(ActionEvent actionEvent) {
        launchGame(GameType.BOT_EASY, actionEvent);
    }

    private void launchGame(GameType gameType, ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
            GameController controller = new GameController(gameType);
            loader.setController(controller);
            Parent root = loader.load();
            stage.setScene(new Scene(root, 500, 500));
        } catch (IOException e) {
            System.err.println("IO error!");
        }
    }

    public void handleLogButtonAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Log");
        alert.setHeaderText(null);
        alert.setContentText(getLog());
        alert.showAndWait();
    }
}
