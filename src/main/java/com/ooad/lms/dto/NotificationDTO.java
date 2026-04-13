package com.ooad.lms.dto;

import java.io.Serializable;

public class NotificationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long notificationId;
    private String title;
    private String message;
    private Long materialId;
    private Long commentId;

    public NotificationDTO(Long notificationId, String title, String message, Long materialId, Long commentId) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.materialId = materialId;
        this.commentId = commentId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public Long getCommentId() {
        return commentId;
    }
}
