package ru.spbau.dkaznacheev.simpleftp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

import static ru.spbau.dkaznacheev.simpleftp.ResponseCode.*;
import static ru.spbau.dkaznacheev.simpleftp.ResponseCode.INVALID_COMMAND;

/**
 * Server class for SimpleFTP. It can send files and folder descriptions.
 */
public class Server {

    /**
     * Number of threads to run thread pool on.
     */
    private static final int THREADS = 4;

    /**
     * A thread pool for describing folders.
     */
    private final ExecutorService folderDescriber = Executors.newFixedThreadPool(THREADS);

    /**
     * Server's ServerSocket.
     */
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
    }

    /**
     * Runnable for multithreaded client handling.
     */
    private class ClientProcessor implements Runnable {
        /**
         * Client's socket.
         */
        private Socket socket;

        private ClientProcessor(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream())
            ) {
                while (true) {
                    int command = in.readInt();
                    String path = in.readUTF();

                    if (command == 0) {
                        out.writeInt(CLOSE_CONNECTION.ordinal());
                        break;
                    }

                    switch (command) {
                        case 1: {
                            Future<FolderDescription> result = folderDescriber.submit(new FolderDescriptionCallable(path));
                            FolderDescription description;
                            try {
                                description = result.get();
                            } catch (Exception e) {
                                description = null;
                            }
                            out.writeInt(FOLDER_DESCRIPTION.ordinal());
                            writeFolderDescription(description, out);
                            break;
                        }
                        case 2: {
                            out.writeInt(FILE_SEND.ordinal());
                            sendFile(path, out);
                            break;
                        }
                        default: {
                            out.writeInt(INVALID_COMMAND.ordinal());
                        }
                    }
                }
            }
            catch (IOException e) { }
            finally {
                try {
                    socket.close();
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    /**
     * Callable for getting FolderDescription in a ThreadPool.
     */
    private class FolderDescriptionCallable implements Callable<FolderDescription> {

        /**
         * Path to folder.
         */
        private String path;

        private FolderDescriptionCallable(String path) {
            this.path = path;
        }

        @Override
        public FolderDescription call() throws Exception {
            return FolderDescription.describeFolder(path);
        }
    }

    /**
     * Starts the server.
     */
    public void start() {
        while(true) {
            try {
                Socket socket = serverSocket.accept();
                new Thread(new ClientProcessor(socket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes FolderDescription to DataOutputStream.
     * @param description description to write
     * @param out output stream to write to
     */
    public void writeFolderDescription(FolderDescription description, DataOutputStream out) throws IOException {
        int size;
        if (description == null) {
            size = 0;
        } else {
            size = description.getSize();
        }
        out.writeInt(size);
        if (size == 0) {
            return;
        }
        for (FolderDescription.FileDescription fileDescription : description.getFiles()) {
            out.writeUTF(fileDescription.getName());
            out.writeBoolean(fileDescription.isDir());
        }
    }

    /**
     * Sends file over ServerSocket's DataOutputStream.
     * @param name filename
     * @param out output stream to write to
     */
    private void sendFile(String name, DataOutputStream out) throws IOException{
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

    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8080);
            new Server(serverSocket).start();
        } catch (IOException e) {
            System.err.println("Error creating server");
            return;
        }
    }

}
