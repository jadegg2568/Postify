package ru.jadegg2568.Postify.exception.file;

public class FileDeleteException extends FileException {

    public FileDeleteException() {
        super("Failed to delete file");
    }
}
