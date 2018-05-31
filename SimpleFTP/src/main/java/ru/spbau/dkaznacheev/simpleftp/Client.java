package ru.spbau.dkaznacheev.simpleftp;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client class for SimpleFTP. It can print folder descriptions and download files.
 */
public class Client {
    /**
     * Code for sending folder description request.
     */
    private static final int FOLDER_CODE = 1;

    /**
     * Code for sending file download request.
     */
    private static final int FILE_CODE = 2;

    /**
     * Client's input stream.
     */
    private final DataInputStream in;

    /**
     * Client's output stream.
     */
    private final DataOutputStream out;

    public Client(Socket socket) throws IOException {
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());//PrintWriter(socket.getOutputStream());
    }

    /**
     * Sends a query to describe folder and recieves a FolderDescription.
     * @param path path to folder
     * @return FolderDescription of a folder
     */
    public FolderDescription describeFolderQuery(String path) throws QueryFormatException, IOException {
        out.writeInt(FOLDER_CODE);
        out.writeUTF(path);
        ResponseCode fromServer = ResponseCode.values()[in.readInt()];
        switch (fromServer) {
            case INVALID_COMMAND: {
                throw new QueryFormatException();
            }
            case FOLDER_DESCRIPTION: {
                return read();
            }
            default: {
                return null;
            }
        }
    }

    /**
     * Sends a query to download file and downloads it.
     * @param filename path to file
     */
    public void getFileQuery(String filename) throws IOException, QueryFormatException {
        out.writeInt(FILE_CODE);
        out.writeUTF(filename);
        ResponseCode fromServer = ResponseCode.values()[in.readInt()];
        switch (fromServer) {
            case INVALID_COMMAND: {
                throw new QueryFormatException();
            }
            case FILE_SEND: {
                receiveFile(filename);
                break;
            }
        }
    }

    /**
     * Reads FolderDescription from InputStream.
     * @return read FolderDescription
     */
    private FolderDescription read() throws IOException {
        int size = in.readInt();
        if (size == 0) {
            return new FolderDescription(0, null);
        }
        FolderDescription.FileDescription[] files = new FolderDescription.FileDescription[size];

        for (int i = 0; i < size; i++)  {
            String name = in.readUTF();
            boolean isDir = in.readBoolean();
            files[i] = new FolderDescription.FileDescription(name, isDir);
        }
        return new FolderDescription(size, files);
    }

    /**
     * Returns simple file name of the filepath.
     * @param path path to file
     * @return filename
     */
    private static String simpleName(String path) {
        return new File(path).getName();
    }

    /**
     * Downloads a file from server.
     * @param name name of the file
     */
    private void receiveFile(String name) throws IOException{
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
     * Console interface for client.
     */
    public static void main(String[] args) {

        try (Socket socket = new Socket("127.0.0.1", 8080);
            Scanner stdIn = new Scanner(System.in)) {
            String query;
            Client client = new Client(socket);
            while ((query = stdIn.nextLine()) != null) {
                String[] queryParts = query.split(" ");
                if (queryParts.length == 2) {
                    if (queryParts[0].equals("1")) {
                        client.describeFolderQuery(queryParts[1]).print();
                    } else if (queryParts[0].equals("2")) {
                        client.getFileQuery(queryParts[1]);
                        System.out.println("Downloaded" + simpleName(queryParts[1]));
                    } else {
                        System.out.println("Incorrect command!");
                    }
                } else {
                    System.out.println("Incorrect input!");
                }
            }
        }  catch (IOException | QueryFormatException e) {
            e.printStackTrace();
        }
    }
}
