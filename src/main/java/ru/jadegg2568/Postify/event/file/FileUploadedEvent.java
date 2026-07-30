package ru.jadegg2568.Postify.event.file;

import lombok.Getter;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.event.BaseEvent;
import ru.jadegg2568.Postify.event.EventType;

@Getter
public class FileUploadedEvent extends FileEvent {
    public FileUploadedEvent(User user, String objectName, String fileName, long fileSize, String contentType) {
        super(EventType.FILE_UPLOADED, user, objectName, fileName, fileSize, contentType);
    }
}