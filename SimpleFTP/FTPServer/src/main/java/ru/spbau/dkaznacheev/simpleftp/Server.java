package ru.spbau.dkaznacheev.simpleftp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (
                ServerSocket serverSocket = new ServerSocket(8080);
                Socket clientSocket = serverSocket.accept();
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()))
        ) {
            out.writeInt(0);
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals("quit")) {
                    out.writeInt(1);
                    break;
                }

                String[] parts = inputLine.split(" ");
                if (parts.length != 2) {
                    out.writeInt(2);
                    continue;
                }
                String command = parts[0];
                String path = parts[1];

                if (command.equals("1")) {
                    FolderDescription description = FolderDescription.describeFolder(path);
                    out.writeInt(3
                    );
                    description.write(out);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
