import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


/**
 * Class containing MD5 hashing methods, single or multithreaded.
 * Hashing rules:
 * hash(dir)  = MD5(name + hash(file1) + ...)
 * hash(file) = MD5(file)
 */
public class MD5 {

    /**
     * Hashes a single non-directory file.
     * @param file file to hash
     * @return hash of a file
     */
    private static @NotNull byte[] fileHash(@NotNull File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] buf = new byte[2048];
        try (FileInputStream inputStream = new FileInputStream(file);
             DigestInputStream digestInputStream = new DigestInputStream(inputStream, digest)
        ) {
            while (digestInputStream.read(buf) > -1);
            return digestInputStream.getMessageDigest().digest();
        }

    }

    /**
     * Hashes a directory or file, runs on a single thread.
     * @param root directory or file to hash
     * @return hash of directory or file.
     */
    public static @NotNull byte[] hashSingleThread(@NotNull File root) throws IOException, NoSuchAlgorithmException {
        if (root.isDirectory()) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(root.getName().getBytes());
            File[] contents = root.listFiles();
            if (contents == null) {
                throw new NotDirectoryException(root.getName());
            }
            for (File file : contents) {
                digest.update(hashSingleThread(file));
            }
            return digest.digest();
        } else {
            return fileHash(root);
        }
    }

    /**
     * Hashes a directory or file, runs on multiple threads.
     * @param root directory or file to hash
     * @return hash of directory or file.
     */
    public static @NotNull byte[] hashMultiThread(@NotNull File root) {
        ForkJoinPool pool = new ForkJoinPool();
        HashRecursiveTask task = new HashRecursiveTask(root);
        return pool.invoke(task);
    }

    /**
     * Recursive task for ForkJoin hashing.
     */
    private static class HashRecursiveTask extends RecursiveTask<byte[]> {

        /**
         * File to hash.
         */
        private File file;

        public HashRecursiveTask(@NotNull File file) {
            this.file = file;
        }

        /**
         * Returns hash of task's directory or file.
         * @return hash of directory or file.
         */
        @Override
        protected @NotNull byte[] compute() {
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                if (file.isDirectory()) {
                    digest.update(file.getName().getBytes());
                    File[] contents = file.listFiles();
                    if (contents == null) {
                        throw new ForkJoinError();
                    }
                    List<HashRecursiveTask> tasks = new LinkedList<>();
                    for (File content : contents) {
                        HashRecursiveTask task = new HashRecursiveTask(content);
                        task.fork();
                        tasks.add(task);
                    }
                    for (HashRecursiveTask task : tasks) {
                        digest.update(task.join());
                    }
                    return digest.digest();
                } else {
                    return fileHash(file);
                }
            } catch (NoSuchAlgorithmException | IOException e) {
                throw new ForkJoinError();
            }
        }
    }

    public static void main(String[] args) {
        File root = new File("./"); //note that it runs in project directory by default
        long startSingleTime = System.nanoTime();
        try {
            hashSingleThread(root);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.err.println("Error running single thread hashing!");
        }
        long endSingleTime = System.nanoTime();
        long startMultiTime = System.nanoTime();
        try {
            hashSingleThread(root);
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Error running multithread hashing!");
        }
        long endMultiTime = System.nanoTime();

        System.out.println("Single Thread Hashing: " + (endSingleTime - startSingleTime) / 1000 + " ms");
        System.out.println("MultiThread Hashing: " + (endMultiTime - startMultiTime) / 1000 + " ms");
    }

}
