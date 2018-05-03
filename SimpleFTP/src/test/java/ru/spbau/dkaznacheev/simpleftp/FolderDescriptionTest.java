package ru.spbau.dkaznacheev.simpleftp;

import org.junit.Test;

public class FolderDescriptionTest {
    @Test
    public void folderDescSimpleTest() {
        FolderDescription desc = FolderDescription.describeFolder("./");
        desc.print();
    }
}
