package ru.spbau.dkaznacheev.simpleftp;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket("127.0.0.1", 8080);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                Scanner stdIn = new Scanner(System.in)
        ) {
            int fromServer;
            String fromUser;
            while ((fromServer = in.readInt()) != 1) {
                if (fromServer == 2) {
                    System.out.println("Invalid command");
                }
                if (fromServer == 3) {
                    FolderDescription description = FolderDescription.read(in);
                    description.print();
                }
                fromUser = stdIn.nextLine();
                if (fromUser != null) {
                    out.println(fromUser);
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

