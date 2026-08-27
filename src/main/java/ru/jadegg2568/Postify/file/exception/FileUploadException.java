package ru.jadegg2568.Postify.file.exception;

public class FileUploadException extends FileException {
    public FileUploadException() {
        super("Failed to upload file");
    }
}
