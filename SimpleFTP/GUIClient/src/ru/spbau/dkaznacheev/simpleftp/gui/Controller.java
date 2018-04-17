package ru.spbau.dkaznacheev.simpleftp.gui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import ru.spbau.dkaznacheev.simpleftp.FolderDescription;
import ru.spbau.dkaznacheev.simpleftp.FolderDescription.FileDescription;
import ru.spbau.dkaznacheev.simpleftp.ResponseCode;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringJoiner;

import static ru.spbau.dkaznacheev.simpleftp.ResponseCode.FILE_SEND;
import static ru.spbau.dkaznacheev.simpleftp.ResponseCode.FOLDER_DESCRIPTION;

public class Controller {
    private String currentPath = ".";

    private PrintWriter out;
    private DataInputStream in;

    @FXML
    VBox buttonContainer;

    /**
     * Downloads a file from sever
     * @param in inputstream of a socket
     * @param name name of the file
     */
    private static void receiveFile(DataInputStream in, String name) throws IOException{
        long length = in.readLong();
        if (length == 0) {
            System.out.println("No such file");
            return;
        }
        try (FileOutputStream out = new FileOutputStream(name)) {
            byte[] buffer = new byte[4096];
            long remaining = length;
            int read;
            while ((read = in.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
                remaining -= read;
                out.write(buffer);
            }
        }
    }

    private boolean initClient() {
        try {
            Socket socket = new Socket("127.0.0.1", 8080);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private static String getNewPath(String path, String name) {
        if (path.equals("./")) {
            return name;
        } else {
            return path + "/" + name;
        }
    }

    private FolderDescription requestFolderDescription(String name) {
        System.out.println("1 " + name);
        out.println("1 " + name);
        try {
            ResponseCode fromServer = ResponseCode.values()[in.readInt()];
            if (fromServer != FOLDER_DESCRIPTION) {
                return null;
            }
            return FolderDescription.read(in);
        } catch (IOException e) {
            return null;
        }
    }

    private boolean getAndShowFolder(String name) {
        FolderDescription desc = requestFolderDescription(name);
        if (desc == null) {
            waitAlert(AlertType.ERROR, "Wrong folder", "OK to close");
            return false;
        }
        if (desc.getSize() == 0) {
            waitAlert(AlertType.INFORMATION, "Empty folder", "OK to close");
            return false;
        }
        refreshFolder(desc.getFiles());
        return true;
    }

    private void getAndDownloadFile(String name) {
        out.println("2 " + getNewPath(currentPath, name));
        try {
            ResponseCode fromServer = ResponseCode.values()[in.readInt()];
            if (fromServer != FILE_SEND) {
                throw new IOException();
            }
            receiveFile(in, name);
            waitAlert(AlertType.INFORMATION, "File downloaded", name + " downloaded!");
        } catch (IOException e) {
            waitAlert(AlertType.ERROR, "Could not download file", "OK to close");
        }
    }

    private void refreshFolder(FileDescription[] files) {
        buttonContainer.getChildren().clear();
        Button backButton = new Button();
        backButton.setAlignment(Pos.BASELINE_LEFT);
        backButton.setPrefWidth(Double.MAX_VALUE);
        backButton.setText("..");
        backButton.setStyle("-fx-background-color: #a5ffcf");
        backButton.setOnAction((e) -> {
            getAndShowFolder(backFolder());
            currentPath = backFolder();
        });
        buttonContainer.getChildren().add(backButton);
        for (FileDescription file : files) {
            Button button = new Button();
            button.setAlignment(Pos.BASELINE_LEFT);
            button.setPrefWidth(Double.MAX_VALUE);
            button.setText(file.name);
            if (file.isDir) {
                button.setStyle("-fx-background-color: #a5ffcf");
                button.setOnAction((e) -> {
                    if (getAndShowFolder(getNewPath(currentPath, file.name))) {
                        currentPath = getNewPath(currentPath, file.name);
                    }
                });
            } else {
                button.setStyle("-fx-background-color: #e2edff");
                button.setOnAction((e) -> getAndDownloadFile(file.name));
            }
            buttonContainer.getChildren().add(button);
        }
    }

    private String backFolder() {
        if (currentPath.equals("./")) {
            return currentPath;
        }
        String[] parts = currentPath.split("/");
        if (parts.length == 2) {
            return "./";
        }
        StringJoiner joiner = new StringJoiner("/");
        for (int i = 0; i < parts.length - 1; i++) {
            joiner.add(parts[i]);
        }
        return joiner.toString();
    }

    private void waitAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        while (!initClient()) {
            waitAlert(AlertType.ERROR,"Connection error", "OK to retry");
        }

        getAndShowFolder(currentPath);
    }
}
