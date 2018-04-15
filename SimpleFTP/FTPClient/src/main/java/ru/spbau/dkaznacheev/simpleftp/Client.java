package ru.spbau.dkaznacheev.simpleftp;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

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
        try (FileOutputStream out = new FileOutputStream(simpleName(name))) {
            byte[] buffer = new byte[4096];
            long remaining = length;
            int read;
            while ((read = in.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
                remaining -= read;
                out.write(buffer);
            }
        }
    }

    /**
     * Returns simple file name of the filepath.
     * @param path path to file
     * @return filename
     */
    private static String simpleName(String path) {
        return new File(path).getName();
    }

    public static void main(String[] args) {
        try (
                Socket socket = new Socket("127.0.0.1", 8080);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                Scanner stdIn = new Scanner(System.in)
        ) {
            ResponseCode fromServer;
            String fromUser;
            boolean exit = false;
            while (!exit) {
                fromUser = stdIn.nextLine();
                if (fromUser != null) {
                    out.println(fromUser);
                }
                fromServer = ResponseCode.values()[in.readInt()];
                switch (fromServer) {
                    case CLOSE_CONNECTION: {
                        exit = true;
                        break;
                    }
                    case INVALID_COMMAND: {
                        System.out.println("Invalid command");
                        break;
                    }
                    case FOLDER_DESCRIPTION: {
                        FolderDescription description = FolderDescription.read(in);
                        description.print();
                        break;
                    }
                    case FILE_SEND: {
                        String[] parts = fromUser.split(" ");
                        receiveFile(in, parts[1]);
                        break;
                    }
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Unknown host");
        } catch (ConnectException e) {
            System.err.println("Connection refused");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

