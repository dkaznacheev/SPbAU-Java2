package ru.spbau.dkaznacheev.simpleftp;

/**
 * Codes that client and server use to send util information.
 */
public enum ResponseCode {
    /**
     * If a connection is closed.
     */
    CLOSE_CONNECTION,

    /**
     * If a connection is opened.
     */
    OPEN_CONNECTION,

    /**
     * If a client sent an invalid command.
     */
    INVALID_COMMAND,

    /**
     * If a server is going to send a FolderDescription.
     */
    FOLDER_DESCRIPTION,

    /**
     * If a server is going to send a file.
     */
    FILE_SEND
}