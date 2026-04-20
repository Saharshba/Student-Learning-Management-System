package com.ooad.lms.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ooad.lms.designpattern.observer.notification.MaterialCommentNotificationEvent;
import com.ooad.lms.designpattern.observer.notification.MaterialCommentNotificationEventType;
import com.ooad.lms.designpattern.observer.notification.MaterialCommentObserver;
import com.ooad.lms.dto.NotificationDTO;
import com.ooad.lms.model.UserNotification;
import com.ooad.lms.repository.InMemoryDataStore;
import com.ooad.lms.repository.NotificationRepository;

@Service
public class NotificationService implements MaterialCommentObserver {
    private final NotificationRepository notificationRepository;
    private final InMemoryDataStore dataStore;

    public NotificationService(NotificationRepository notificationRepository, InMemoryDataStore dataStore) {
        this.notificationRepository = notificationRepository;
        this.dataStore = dataStore;
    }

    @Override
    public void onMaterialCommentEvent(MaterialCommentNotificationEvent event) {
        if (event.type() == MaterialCommentNotificationEventType.COMMENT_ASKED) {
            notifyCommentAsked(
                    event.instructorId(),
                    event.commentAuthorId(),
                    event.commentAuthorName(),
                    event.materialId(),
                    event.materialName(),
                    event.commentId(),
                    event.message()
            );
            return;
        }

        if (event.type() == MaterialCommentNotificationEventType.INSTRUCTOR_REPLIED) {
            notifyInstructorReply(
                    event.commentAuthorId(),
                    event.commentAuthorName(),
                    event.replierId(),
                    event.replierName(),
                    event.materialId(),
                    event.materialName(),
                    event.commentId(),
                    event.message()
            );
            return;
        }

        if (event.type() == MaterialCommentNotificationEventType.STUDENT_REPLIED) {
            notifyStudentReply(
                    event.commentAuthorId(),
                    event.commentAuthorName(),
                    event.instructorId(),
                    event.replierName(),
                    event.materialId(),
                    event.materialName(),
                    event.commentId(),
                    event.message()
            );
        }
    }

    public void notifyCommentAsked(Long instructorId, Long studentId, String studentName, Long materialId, String materialName, Long commentId, String message) {
        if (instructorId != null) {
            create(instructorId, "COMMENT_ASKED", "New question on " + materialName,
                    studentName + ": " + message, materialId, commentId);
        }
        create(studentId, "COMMENT_ASKED", "Question posted on " + materialName,
                "Your question is now public and visible to the instructor.", materialId, commentId);
    }

    public void notifyInstructorReply(Long studentId, String studentName, Long instructorId, String instructorName, Long materialId, String materialName, Long commentId, String reply) {
        if (studentId != null) {
            create(studentId, "COMMENT_REPLIED", "Reply on " + materialName,
                    instructorName + ": " + reply, materialId, commentId);
        }
    }

    public void notifyStudentReply(Long studentId, String studentName, Long instructorId, String instructorName, Long materialId, String materialName, Long commentId, String reply) {
        if (instructorId != null) {
            create(instructorId, "COMMENT_REPLIED", "New reply on " + materialName,
                    studentName + ": " + reply, materialId, commentId);
        }
        if (studentId != null) {
            create(studentId, "COMMENT_REPLIED", "Reply on " + materialName,
                    studentName + ": " + reply, materialId, commentId);
        }
    }

    public List<String> getMessagesForUser(Long userId) {
        List<String> messages = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .map(notification -> notification.getTitle() + " - " + notification.getMessage())
                .toList();
        notificationRepository.markAllAsRead(userId);
        return messages;
    }

    public List<NotificationDTO> getNotificationsForUser(Long userId) {
        List<NotificationDTO> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .map(notification -> new NotificationDTO(
                        notification.getNotificationId(),
                        notification.getTitle(),
                        notification.getMessage(),
                        notification.getSourceMaterialId(),
                        notification.getSourceCommentId()
                ))
                .toList();
        notificationRepository.markAllAsRead(userId);
        return notifications;
    }

    public long getUnreadCountForUser(Long userId) {
        return notificationRepository.countUnreadByRecipientId(userId);
    }

    public List<String> getUnreadMessagesForUser(Long userId) {
        List<String> messages = new ArrayList<>();
        notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .filter(notification -> !notification.isRead())
                .forEach(notification -> messages.add(notification.getTitle() + " - " + notification.getMessage()));
        return messages;
    }

    private void create(Long recipientId, String type, String title, String message, Long materialId, Long commentId) {
        if (recipientId == null) {
            return;
        }
        Long notificationId = dataStore.nextNotificationId();
        notificationRepository.save(new UserNotification(
                notificationId,
                recipientId,
                type,
                title,
                message,
                materialId,
                commentId,
                LocalDateTime.now()
        ));
    }
}