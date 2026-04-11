package com.ooad.lms.repository;

import com.ooad.lms.model.Submission;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SubmissionRepository {
    private final InMemoryDataStore dataStore;

    public SubmissionRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<Submission> findByStudentIdOrderByTimestampDesc(Long studentId) {
        return dataStore.submissions().values().stream()
                .filter(submission -> submission.getStudentId().equals(studentId))
                .sorted(Comparator.comparing(Submission::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public Optional<Submission> findById(Long id) {
        return Optional.ofNullable(dataStore.submissions().get(id));
    }

    public Submission save(Submission submission) {
        if (submission.getSubmissionId() == null) {
            submission = new Submission(dataStore.nextSubmissionId(), submission.getStudentId(),
                    submission.getAssignmentId(), submission.getTimestamp(), submission.getContent());
        }
        dataStore.submissions().put(submission.getSubmissionId(), submission);
        dataStore.persist();
        return submission;
    }

    public Submission saveAndFlush(Submission submission) {
        return save(submission);
    }
}
