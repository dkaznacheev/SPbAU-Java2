package ru.spbau.dkaznacheev.simpleftp;

import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.PatternSyntaxException;

import static org.junit.Assert.*;
import static ru.spbau.dkaznacheev.simpleftp.ResponseCode.FILE_SEND;
import static ru.spbau.dkaznacheev.simpleftp.ResponseCode.FOLDER_DESCRIPTION;

public class SimpleFTPTest {
    private static class ServerMockup {
        private static final byte[] FILE_CONTENT = {0, 1};
        private static void runServer(BiConsumer<DataInputStream, DataOutputStream> processor) {
            try (
                    ServerSocket serverSocket = new ServerSocket(8080);
                    Socket socket = serverSocket.accept();
                    DataInputStream is = new DataInputStream(socket.getInputStream());
                    DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            ) {
                processor.accept(is, os);
            } catch (Exception e) {}
        }

        private static void returnEmptyDescription() {
            runServer((is, os) -> {
                try {
                    is.readInt();
                    is.readUTF();
                    os.writeInt(FOLDER_DESCRIPTION.ordinal());
                    os.writeInt(0);
                } catch (Exception e) {}
            });
        }

        private static void returnCorrectDescription() {
            runServer((is, os) -> {
                try {
                    is.readInt();
                    is.readUTF();
                    os.writeInt(FOLDER_DESCRIPTION.ordinal());
                    os.writeInt(1);
                    os.writeUTF("file");
                    os.writeBoolean(false);
                } catch (Exception e) {}
            });
        }

        private static void sendFile() {
            runServer((is, os) -> {
                try {
                    is.readInt();
                    is.readUTF();
                    os.writeInt(FILE_SEND.ordinal());
                    os.writeLong(FILE_CONTENT.length);
                    os.write(FILE_CONTENT);
                } catch (Exception e) {}
            });
        }
    }

    @Test
    public void emptyFolderDescriptionClientTest() throws Throwable {
        Thread thread = new Thread(ServerMockup::returnEmptyDescription);
        thread.start();
        Client client = new Client( new Socket("127.0.0.1", 8080));
        FolderDescription description = client.describeFolderQuery("");
        assertEquals(0, description.getSize());
        assertNull(description.getFiles());
        thread.join();
    }

    @Test
    public void correctFolderDescriptionClientTest() throws Throwable {
        Thread thread = new Thread(ServerMockup::returnCorrectDescription);
        thread.start();
        Client client = new Client( new Socket("127.0.0.1", 8080));
        FolderDescription description = client.describeFolderQuery("");
        assertEquals(1, description.getSize());
        assertEquals(1, description.getFiles().length);
        assertEquals("file", description.getFiles()[0].getName());
        assertEquals(false, description.getFiles()[0].isDir());
        thread.join();
    }

    @Test
    public void correctFileTest() throws Throwable {
        Thread thread = new Thread(ServerMockup::sendFile);
        thread.start();
        Client client = new Client( new Socket("127.0.0.1", 8080));
        client.getFileQuery("file");
        byte[] result = Files.readAllBytes(Paths.get("file"));
        Files.deleteIfExists(Paths.get("file"));
        assertEquals(ServerMockup.FILE_CONTENT[0], result[0]);
        assertEquals(ServerMockup.FILE_CONTENT[1], result[1]);
        thread.join();
    }

    @Test
    public void correctFolderDescription() throws Throwable {
        FolderDescription description = FolderDescription.describeFolder("src/test/resources");
        assertEquals(1, description.getSize());
        assertEquals(1, description.getFiles().length);
        assertEquals("testFile", description.getFiles()[0].getName());
        assertEquals(false, description.getFiles()[0].isDir());
    }
}