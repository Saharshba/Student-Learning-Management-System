package com.ooad.lms.repository;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ooad.lms.model.UserNotification;

@Repository
public class NotificationRepository {
    private final InMemoryDataStore dataStore;

    public NotificationRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public UserNotification save(UserNotification notification) {
        if (notification.getNotificationId() == null) {
            throw new IllegalArgumentException("Notification ID must be set before saving.");
        }
        dataStore.notifications().put(notification.getNotificationId(), notification);
        dataStore.persist();
        return notification;
    }

    public List<UserNotification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId) {
        return dataStore.notifications().values().stream()
                .filter(notification -> notification.getRecipientId().equals(recipientId))
                .sorted(Comparator.comparing(UserNotification::getCreatedAt).reversed())
                .toList();
    }

    public long countUnreadByRecipientId(Long recipientId) {
        return dataStore.notifications().values().stream()
                .filter(notification -> notification.getRecipientId().equals(recipientId))
                .filter(notification -> !notification.isRead())
                .count();
    }

    public void markAllAsRead(Long recipientId) {
        dataStore.notifications().values().stream()
                .filter(notification -> notification.getRecipientId().equals(recipientId))
                .filter(notification -> !notification.isRead())
                .forEach(notification -> notification.setRead(true));
        dataStore.persist();
    }
}