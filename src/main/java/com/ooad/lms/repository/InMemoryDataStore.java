package com.ooad.lms.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.ooad.lms.model.Assignment;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.Submission;
import com.ooad.lms.model.User;

@Component
public class InMemoryDataStore {
    private final AtomicLong userIds = new AtomicLong(1);
    private final AtomicLong courseIds = new AtomicLong(1);
    private final AtomicLong moduleIds = new AtomicLong(1);
    private final AtomicLong materialIds = new AtomicLong(1);
    private final AtomicLong assignmentIds = new AtomicLong(1);
    private final AtomicLong examIds = new AtomicLong(1);
    private final AtomicLong submissionIds = new AtomicLong(1);

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, Course> courses = new ConcurrentHashMap<>();
    private final Map<Long, Assignment> assignments = new ConcurrentHashMap<>();
    private final Map<Long, Submission> submissions = new ConcurrentHashMap<>();

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

    public long nextExamId() {
        return examIds.getAndIncrement();
    }

    public long nextSubmissionId() {
        return submissionIds.getAndIncrement();
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

    public Map<Long, Submission> submissions() {
        return submissions;
    }
}