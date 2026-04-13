package com.ooad.lms.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserNotification implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long notificationId;
    private Long recipientId;
    private String type;
    private String title;
    private String message;
    private Long sourceMaterialId;
    private Long sourceCommentId;
    private LocalDateTime createdAt;
    private boolean read;

    public UserNotification(Long notificationId, Long recipientId, String type, String title, String message,
                            Long sourceMaterialId, Long sourceCommentId, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.recipientId = recipientId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.sourceMaterialId = sourceMaterialId;
        this.sourceCommentId = sourceCommentId;
        this.createdAt = createdAt;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Long getSourceMaterialId() {
        return sourceMaterialId;
    }

    public Long getSourceCommentId() {
        return sourceCommentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}