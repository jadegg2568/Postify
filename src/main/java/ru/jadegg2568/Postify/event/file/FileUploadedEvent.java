package ru.jadegg2568.Postify.event.file;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.BaseEvent;

@Getter
public class FileUploadedEvent extends FileEvent {
    public FileUploadedEvent(User user, String objectName, String fileName, long fileSize, String contentType) {
        super(user, objectName, fileName, fileSize, contentType);
    }
}