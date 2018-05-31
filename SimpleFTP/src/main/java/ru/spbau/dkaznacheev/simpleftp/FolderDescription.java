package ru.spbau.dkaznacheev.simpleftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Class describing folder contents.
 */
public class FolderDescription {

    /**
     * Size of the folder.
     */
    private final int size;

    /**
     * Description of folder's contents.
     */
    private final FileDescription[] files;

    public FolderDescription(int size, FileDescription[] files) {
        this.size = size;
        this.files = files;
    }

    public int getSize() {
        return size;
    }

    public FileDescription[] getFiles() {
        return files;
    }

    /**
     * Prints FolderDescription to console.
     */
    public void print() {
        if (size == 0) {
            System.out.println("Folder not found");
            return;
        }
        System.out.println(size + " files:");
        for (FileDescription description : files) {
            System.out.print(description.name);
            if (description.isDir) {
                System.out.print(": folder");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Returns a FolderDescription from path to folder.
     * If it is the path of non-folder file, returns a FolderDescription with size = 0.
     * @param path path to file/folder
     * @return FolderDescription on this path
     */
    public static FolderDescription describeFolder(String path) {
        File root = new File(path);

        File[] contents = root.listFiles();
        if (contents == null) {
            return new FolderDescription(0, null);
        }

        FileDescription[] files = new FileDescription[contents.length];
        int size = contents.length;
        for (int i = 0; i < size; i++) {
            String name = contents[i].getName();
            boolean isDir = contents[i].isDirectory();
            files[i] = new FileDescription(name, isDir);
        }
        return new FolderDescription(size, files);
    }

    /**
     * Class describing one file.
     */
    public static class FileDescription {

        /**
         * Filename.
         */
        private final String name;

        /**
         * Whether a file is a directory or not.
         */
        private final boolean isDir;

        public String getName() {
            return name;
        }

        public boolean isDir() {
            return isDir;
        }

        public FileDescription(String name, boolean isDir) {
            this.name = name;
            this.isDir = isDir;
        }
    }
}
