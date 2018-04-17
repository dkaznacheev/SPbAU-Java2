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
     * Class describing one file.
     */
    public static class FileDescription {

        /**
         * Filename.
         */
        public final String name;

        /**
         * Whether a file is a directory or not.
         */
        public final boolean isDir;

        public FileDescription(String name, boolean isDir) {
            this.name = name;
            this.isDir = isDir;
        }
    }

    public FileDescription[] getFiles() {
        return files;
    }

    public int getSize() {
        return size;
    }

    /**
     * Size of the folder.
     */
    private int size;

    /**
     * Description of folder's contents.
     */
    private FileDescription[] files;

    /**
     * Writes FolderDescription to DataOutputStream.
     * @param out stream to write to
     */
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(size);
        if (size == 0) {
            return;
        }
        for (FileDescription description : files) {
            out.writeUTF(description.name);
            out.writeBoolean(description.isDir);
        }
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
     * Reads FolderDescription from InputStream.
     * @param in input stream
     * @return read FolderDescription
     */
    public static FolderDescription read(DataInputStream in) throws IOException {
        FolderDescription description = new FolderDescription();
        description.size = in.readInt();
        if (description.size == 0) {
            return description;
        }
        description.files = new FileDescription[description.size];

        for (int i = 0; i < description.size; i++)  {
            String name = in.readUTF();
            boolean isDir = in.readBoolean();
            description.files[i] = new FileDescription(name, isDir);
        }
        return description;
    }

    /**
     * Returns a FolderDescription from path to folder.
     * If it is the path of non-folder file, returns a FolderDescription with size = 0.
     * @param path path to file/folder
     * @return FolderDescription on this path
     */
    public static FolderDescription describeFolder(String path) {
        File root = new File(path);
        FolderDescription result = new FolderDescription();

        File[] contents = root.listFiles();
        if (contents == null) {
            result.size = 0;
            return result;
        }

        result.files = new FileDescription[contents.length];
        result.size = contents.length;
        for (int i = 0; i < contents.length; i++) {
            String name = contents[i].getName();
            boolean isDir = contents[i].isDirectory();
            result.files[i] = new FileDescription(name, isDir);
        }
        return result;
    }
}
