package ru.jadegg2568.Postify.exception.file;

public class FileUploadException extends FileException {
    public FileUploadException() {
        super("Failed to upload file");
    }
}
