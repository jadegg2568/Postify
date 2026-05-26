package ru.jadegg2568.Postify.exception.file;

public class FileDownloadException extends FileException {
    public FileDownloadException() {
        super("Failed to download file");
    }
}
