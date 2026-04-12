package com.ooad.lms.repository;

import com.ooad.lms.model.SubmissionFileMetadata;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SubmissionFileMetadataRepository {
    private final InMemoryDataStore dataStore;

    public SubmissionFileMetadataRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Optional<SubmissionFileMetadata> findById(Long id) {
        return Optional.ofNullable(dataStore.submissionFileMetadata().get(id));
    }

    public Optional<SubmissionFileMetadata> findBySubmissionIdAndStudentId(Long submissionId, Long studentId) {
        return dataStore.submissionFileMetadata().values().stream()
                .filter(metadata -> metadata.getSubmissionId().equals(submissionId) && metadata.getStudentId().equals(studentId))
                .findFirst();
    }

    public Optional<SubmissionFileMetadata> findBySubmissionId(Long submissionId) {
        return dataStore.submissionFileMetadata().values().stream()
                .filter(metadata -> metadata.getSubmissionId().equals(submissionId))
                .findFirst();
    }

    public boolean existsBySubmissionId(Long submissionId) {
        return dataStore.submissionFileMetadata().values().stream()
                .anyMatch(metadata -> metadata.getSubmissionId().equals(submissionId));
    }

    public SubmissionFileMetadata save(SubmissionFileMetadata metadata) {
        dataStore.submissionFileMetadata().put(metadata.getSubmissionId(), metadata);
        dataStore.persist();
        return metadata;
    }
}
