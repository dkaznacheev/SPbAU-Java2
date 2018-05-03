package ru.spbau.dkaznacheev.simpleftp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import static ru.spbau.dkaznacheev.simpleftp.ResponseCode.*;

/**
 * Simple FTP server that can handle multiple clients and send files over sockets.
 */
public class Server {

    /**
     * Sends file over ServerSocket's DataOutputStream.
     * @param name filename
     * @param out output stream
     */
    private static void sendFile(String name, DataOutputStream out) throws IOException{
        File file = new File(name);
        if (!file.exists()) {
            out.writeLong(0);
            return;
        }
        out.writeLong(file.length());
        byte[] buffer = new byte[4096];
        int read;
        try (FileInputStream in = new FileInputStream(file)) {
            while ((read = in.read(buffer)) > -1) {
                out.write(buffer, 0, read);
            }
        }

    }

    /**
     * Client handler, runs in a separate thread as long as there is a connectin with  a client.
     */
    private static class FTPThread extends Thread {

        /**
         * Socket f server-client connection
         */
        private Socket socket;

        public FTPThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()))
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.equals("0")) {
                        out.writeInt(CLOSE_CONNECTION.ordinal());
                        break;
                    }

                    String[] parts = inputLine.split(" ");
                    if (parts.length != 2) {
                        out.writeInt(INVALID_COMMAND.ordinal());
                        continue;
                    }
                    String command = parts[0];
                    String path = parts[1];

                    if (command.equals("1")) {
                        FolderDescription description = FolderDescription.describeFolder(path);
                        out.writeInt(FOLDER_DESCRIPTION.ordinal());
                        description.write(out);
                    } else if (command.equals("2")) {
                        out.writeInt(FILE_SEND.ordinal());
                        sendFile(path, out);
                    } else {
                        out.writeInt(INVALID_COMMAND.ordinal());
                    }
                }
                socket.close();
            } catch (SocketException e) {
                System.out.println("Goodbye");
            }
            catch (IOException e) {
                System.err.println("IOException on thread " + Thread.currentThread().getName());
            }
        }
    }

    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            System.err.println("Error creating server");
            return;
        }
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new FTPThread(clientSocket).start();
            } catch (IOException e) {
                System.err.println("Exception on client connecting");
            }
        }
    }
}
