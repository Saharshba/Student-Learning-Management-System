package com.ooad.lms.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProgressTracker implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private int totalModules;
    private int completedModules;

    public ProgressTracker(int totalModules, int completedModules) {
        this.totalModules = totalModules;
        this.completedModules = completedModules;
    }

    public double calculateProgress() {
        if (totalModules == 0) {
            return 0.0;
        }
        return (completedModules * 100.0) / totalModules;
    }

    public List<String> notifyDeadlines(List<Assignment> assignments) {
        List<String> reminders = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextThreeDays = now.plusDays(3);
        for (Assignment assignment : assignments) {
            if (assignment.getDeadline().isAfter(now) && assignment.getDeadline().isBefore(nextThreeDays)) {
                reminders.add("Upcoming deadline for assignment " + assignment.getAssignmentId() + " on " + assignment.getDeadline());
            }
        }
        return reminders;
    }

    public int getTotalModules() {
        return totalModules;
    }

    public int getCompletedModules() {
        return completedModules;
    }
}