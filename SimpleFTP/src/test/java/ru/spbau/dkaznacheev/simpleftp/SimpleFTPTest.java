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
    @Test
    public void correctFolderDescription() throws Throwable {
        FolderDescription description = FolderDescription.describeFolder("src/test/resources");
        assertEquals(1, description.getSize());
        assertEquals(1, description.getFiles().length);
        assertEquals("testFile", description.getFiles()[0].getName());
        assertEquals(false, description.getFiles()[0].isDir());
    }
}