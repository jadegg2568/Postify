package ru.jadegg2568.Postify.file.event;

import lombok.Getter;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.event.BaseEvent;
import ru.jadegg2568.Postify.event.EventType;

@Getter
public abstract class FileEvent extends BaseEvent {
    private final User user;
    private final String objectName;
    private final String fileName;
    private final long fileSize;
    private final String contentType;

    protected FileEvent(EventType type, User user, String objectName, String fileName, long fileSize, String contentType) {
        super(type, user.getUuid());
        this.user = user;
        this.objectName = objectName;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }
}