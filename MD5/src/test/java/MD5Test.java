import org.junit.Test;

import java.io.*;
import java.util.Random;

import static org.junit.Assert.*;

public class MD5Test {
    @Test
    public void singleMD5equalsMultiMD5test() throws Exception {
        File root = new File("./");
        byte[] hash1 = MD5.hashSingleThread(root);
        byte[] hash2 = MD5.hashMultiThread(root);
        root.delete();
        assertArrayEquals(hash1, hash2);
    }

    @Test
    public void singleMD5DoesNotChange() throws Exception {
        File root = new File("./");
        byte[] hash1 = MD5.hashSingleThread(root);
        byte[] hash2 = MD5.hashSingleThread(root);
        root.delete();
        assertArrayEquals(hash1, hash2);
    }

    @Test
    public void multiMD5DoesNotChange() throws Exception {
        File root = new File("./");
        byte[] hash1 = MD5.hashMultiThread(root);
        byte[] hash2 = MD5.hashMultiThread(root);
        root.delete();
        assertArrayEquals(hash1, hash2);
    }

    @Test
    public void singleFileMD5hash() throws Exception {
        createFile("test.tmp", 20);
        File root = new File("test.tmp");
        byte[] hash1 = MD5.hashSingleThread(root);
        byte[] hash2 = MD5.hashMultiThread(root);
        root.delete();
        assertArrayEquals(hash1, hash2);
    }

    @Test
    public void emptyFileMD5hash() throws Exception {
        createFile("empty.tmp", 0);
        File root = new File("empty.tmp");
        byte[] hash1 = MD5.hashSingleThread(root);
        byte[] hash2 = MD5.hashMultiThread(root);
        root.delete();
        assertArrayEquals(hash1, hash2);
    }

    @Test
    public void emptyDirectoryMD5hash() throws Exception {
        File root = new File("emptydir");
        root.mkdirs();
        byte[] hash1 = MD5.hashSingleThread(root);
        byte[] hash2 = MD5.hashMultiThread(root);
        root.delete();
        assertArrayEquals(hash1, hash2);
    }

    @Test (expected = IOException.class)
    public void noSuchFileSingleThrows() throws Exception {
        File root = new File("nosuchdir");
        MD5.hashSingleThread(root);
    }

    @Test (expected = IOException.class)
    public void noSuchFileMultiThrows() throws Exception {
        File root = new File("nosuchdir");
        MD5.hashSingleThread(root);
    }

    private void createFile(String name, int length) throws IOException {
        File file = new File(name);
        file.createNewFile();
        Random random = new Random();
        try (
                OutputStream outputStream = new FileOutputStream(file, false);
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            for (int i = 0; i < length; i++) {
                dataOutputStream.writeByte((byte) random.nextInt());
            }
        }
    }

}