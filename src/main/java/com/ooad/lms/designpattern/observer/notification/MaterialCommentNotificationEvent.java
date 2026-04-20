package com.ooad.lms.designpattern.observer.notification;

public record MaterialCommentNotificationEvent(
        MaterialCommentNotificationEventType type,
        Long instructorId,
        Long commentAuthorId,
        String commentAuthorName,
        Long replierId,
        String replierName,
        Long materialId,
        String materialName,
        Long commentId,
        String message
) {
    public static MaterialCommentNotificationEvent commentAsked(
            Long instructorId,
            Long studentId,
            String studentName,
            Long materialId,
            String materialName,
            Long commentId,
            String message
    ) {
        return new MaterialCommentNotificationEvent(
                MaterialCommentNotificationEventType.COMMENT_ASKED,
                instructorId,
                studentId,
                studentName,
                null,
                null,
                materialId,
                materialName,
                commentId,
                message
        );
    }

    public static MaterialCommentNotificationEvent instructorReplied(
            Long studentId,
            String studentName,
            Long instructorId,
            String instructorName,
            Long materialId,
            String materialName,
            Long commentId,
            String reply
    ) {
        return new MaterialCommentNotificationEvent(
                MaterialCommentNotificationEventType.INSTRUCTOR_REPLIED,
                instructorId,
                studentId,
                studentName,
                instructorId,
                instructorName,
                materialId,
                materialName,
                commentId,
                reply
        );
    }

    public static MaterialCommentNotificationEvent studentReplied(
            Long originalCommentAuthorId,
            String originalCommentAuthorName,
            Long studentReplierId,
            String studentReplierName,
            Long instructorId,
            Long materialId,
            String materialName,
            Long commentId,
            String reply
    ) {
        return new MaterialCommentNotificationEvent(
                MaterialCommentNotificationEventType.STUDENT_REPLIED,
                instructorId,
                originalCommentAuthorId,
                originalCommentAuthorName,
                studentReplierId,
                studentReplierName,
                materialId,
                materialName,
                commentId,
                reply
        );
    }
}
