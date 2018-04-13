package ru.spbau.dkaznacheev.simpleftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class FolderDescription {
    private static class FileDescription {
        public String name;
        public boolean isDir;

        public FileDescription(String name, boolean isDir) {
            this.name = name;
            this.isDir = isDir;
        }
    }

    private int size;
    private FileDescription[] files;

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(size);
        if (size == -1) {
            return;
        }
        for (FileDescription description : files) {
            out.writeUTF(description.name);
            out.writeBoolean(description.isDir);
        }
    }

    public void print() {
        if (size == -1) {
            System.out.println("Not a folder");
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

    public static FolderDescription read(DataInputStream in) throws IOException {
        FolderDescription description = new FolderDescription();
        description.size = in.readInt();
        if (description.size == -1) {
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

    public static FolderDescription describeFolder(String path) {
        File root = new File(path);
        FolderDescription result = new FolderDescription();

        File[] contents = root.listFiles();
        if (contents == null) {
            result.size = -1;
            return result;
        }

        result.files = new FileDescription[contents.length];

        for (int i = 0; i < contents.length; i++) {
            String name = contents[i].getName();
            boolean isDir = contents[i].isDirectory();
            result.files[i] = new FileDescription(name, isDir);
        }
        return result;
    }
}
