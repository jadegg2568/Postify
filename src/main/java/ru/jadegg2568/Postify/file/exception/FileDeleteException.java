package ru.jadegg2568.Postify.file.exception;

public class FileDeleteException extends FileException {

    public FileDeleteException() {
        super("Failed to delete file");
    }
}
