package com.ooad.lms.repository;

import com.ooad.lms.model.Assignment;
import com.ooad.lms.model.AssignmentFileMetadata;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.MaterialFileMetadata;
import com.ooad.lms.model.UserNotification;
import com.ooad.lms.model.Submission;
import com.ooad.lms.model.SubmissionFileMetadata;
import com.ooad.lms.model.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryDataStore implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AtomicLong userIds = new AtomicLong(1);
    private final AtomicLong courseIds = new AtomicLong(1);
    private final AtomicLong moduleIds = new AtomicLong(1);
    private final AtomicLong materialIds = new AtomicLong(1);
    private final AtomicLong assignmentIds = new AtomicLong(1);
    private final AtomicLong submissionIds = new AtomicLong(1);

    private final AtomicLong assignmentFileMetadataIds = new AtomicLong(1);
    private final AtomicLong materialFileMetadataIds = new AtomicLong(1);
    private final AtomicLong submissionFileMetadataIds = new AtomicLong(1);
    private final AtomicLong materialCommentIds = new AtomicLong(1);
    private final AtomicLong notificationIds = new AtomicLong(1);

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, Course> courses = new ConcurrentHashMap<>();
    private final Map<Long, Assignment> assignments = new ConcurrentHashMap<>();
    private final Map<Long, Submission> submissions = new ConcurrentHashMap<>();
    private final Map<Long, AssignmentFileMetadata> assignmentFileMetadata = new ConcurrentHashMap<>();
    private final Map<Long, MaterialFileMetadata> materialFileMetadata = new ConcurrentHashMap<>();
    private final Map<Long, SubmissionFileMetadata> submissionFileMetadata = new ConcurrentHashMap<>();
    private final Map<Long, com.ooad.lms.model.MaterialComment> materialComments = new ConcurrentHashMap<>();
    private final Map<Long, UserNotification> notifications = new ConcurrentHashMap<>();

    private transient Path persistenceFile;

    public InMemoryDataStore() {
        this.persistenceFile = Paths.get("data", "store.ser").toAbsolutePath().normalize();
        try {
            Files.createDirectories(persistenceFile.getParent());
        } catch (IOException ex) {
            throw new RuntimeException("Could not create data storage directory.", ex);
        }
    }

    @PostConstruct
    public void loadState() {
        if (!Files.exists(persistenceFile)) {
            return;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(persistenceFile))) {
            Object loaded = inputStream.readObject();
            if (loaded instanceof InMemoryDataStore stored) {
                copyFrom(stored);
                repairAssignmentsFromCourses();
            }
        } catch (IOException | ClassNotFoundException | ClassCastException ex) {
            try {
                Files.deleteIfExists(persistenceFile);
            } catch (IOException ignored) {
            }
        }
    }

    @PreDestroy
    public void saveState() {
        persist();
    }

    public synchronized void persist() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(persistenceFile))) {
            outputStream.writeObject(this);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to persist LMS state.", ex);
        }
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        this.persistenceFile = Paths.get("data", "store.ser").toAbsolutePath().normalize();
    }

    private void copyFrom(InMemoryDataStore other) {
        userIds.set(other.userIds.get());
        courseIds.set(other.courseIds.get());
        moduleIds.set(other.moduleIds.get());
        materialIds.set(other.materialIds.get());
        assignmentIds.set(other.assignmentIds.get());
        submissionIds.set(other.submissionIds.get());
        assignmentFileMetadataIds.set(other.assignmentFileMetadataIds == null ? 1 : other.assignmentFileMetadataIds.get());
        materialFileMetadataIds.set(other.materialFileMetadataIds == null ? 1 : other.materialFileMetadataIds.get());
        submissionFileMetadataIds.set(other.submissionFileMetadataIds == null ? 1 : other.submissionFileMetadataIds.get());
        materialCommentIds.set(other.materialCommentIds == null ? 1 : other.materialCommentIds.get());
        notificationIds.set(other.notificationIds == null ? 1 : other.notificationIds.get());

        users.clear();
        if (other.users != null) {
            users.putAll(other.users);
        }

        courses.clear();
        if (other.courses != null) {
            courses.putAll(other.courses);
        }

        assignments.clear();
        if (other.assignments != null) {
            assignments.putAll(other.assignments);
        }

        submissions.clear();
        if (other.submissions != null) {
            submissions.putAll(other.submissions);
        }

        assignmentFileMetadata.clear();
        if (other.assignmentFileMetadata != null) {
            assignmentFileMetadata.putAll(other.assignmentFileMetadata);
        }

        materialFileMetadata.clear();
        if (other.materialFileMetadata != null) {
            materialFileMetadata.putAll(other.materialFileMetadata);
        }

        submissionFileMetadata.clear();
        if (other.submissionFileMetadata != null) {
            submissionFileMetadata.putAll(other.submissionFileMetadata);
        }

        materialComments.clear();
        if (other.materialComments != null) {
            materialComments.putAll(other.materialComments);
        }

        notifications.clear();
        if (other.notifications != null) {
            notifications.putAll(other.notifications);
        }
    }

    public long nextUserId() {
        return userIds.getAndIncrement();
    }

    public long nextCourseId() {
        return courseIds.getAndIncrement();
    }

    public long nextModuleId() {
        return moduleIds.getAndIncrement();
    }

    public long nextMaterialId() {
        return materialIds.getAndIncrement();
    }

    public long nextAssignmentId() {
        return assignmentIds.getAndIncrement();
    }

    public long nextSubmissionId() {
        return submissionIds.getAndIncrement();
    }

    public long nextAssignmentFileMetadataId() {
        return assignmentFileMetadataIds.getAndIncrement();
    }

    public long nextMaterialFileMetadataId() {
        return materialFileMetadataIds.getAndIncrement();
    }

    public long nextSubmissionFileMetadataId() {
        return submissionFileMetadataIds.getAndIncrement();
    }

    public long nextMaterialCommentId() {
        return materialCommentIds.getAndIncrement();
    }

    public long nextNotificationId() {
        return notificationIds.getAndIncrement();
    }

    public Map<Long, User> users() {
        return users;
    }

    public Map<Long, Course> courses() {
        return courses;
    }

    public Map<Long, Assignment> assignments() {
        return assignments;
    }

    private void repairAssignmentsFromCourses() {
        long maxId = assignmentIds.get();
        for (Course course : courses.values()) {
            for (Assignment assignment : course.getAssignments()) {
                if (assignment.getAssignmentId() == null) {
                    assignment.setAssignmentId(nextAssignmentId());
                }
                assignments.putIfAbsent(assignment.getAssignmentId(), assignment);
                maxId = Math.max(maxId, assignment.getAssignmentId());
            }
        }
        assignmentIds.set(maxId + 1);
    }

    public Map<Long, Submission> submissions() {
        return submissions;
    }

    public Map<Long, AssignmentFileMetadata> assignmentFileMetadata() {
        return assignmentFileMetadata;
    }

    public Map<Long, MaterialFileMetadata> materialFileMetadata() {
        return materialFileMetadata;
    }

    public Map<Long, SubmissionFileMetadata> submissionFileMetadata() {
        return submissionFileMetadata;
    }

    public Map<Long, com.ooad.lms.model.MaterialComment> materialComments() {
        return materialComments;
    }

    public Map<Long, UserNotification> notifications() {
        return notifications;
    }
}