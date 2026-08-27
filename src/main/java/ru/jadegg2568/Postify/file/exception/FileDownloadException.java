package ru.jadegg2568.Postify.file.exception;

public class FileDownloadException extends FileException {
    public FileDownloadException() {
        super("Failed to download file");
    }
}
