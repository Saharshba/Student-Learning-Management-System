package com.ooad.lms.designpattern.observer.notification;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class MaterialCommentEventPublisher {
    private final List<MaterialCommentObserver> observers;

    public MaterialCommentEventPublisher(List<MaterialCommentObserver> observers) {
        this.observers = observers;
    }

    public void publish(MaterialCommentNotificationEvent event) {
        for (MaterialCommentObserver observer : observers) {
            observer.onMaterialCommentEvent(event);
        }
    }
}
