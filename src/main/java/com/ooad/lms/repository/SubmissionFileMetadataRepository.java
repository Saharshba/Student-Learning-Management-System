package com.ooad.lms.repository;

import com.ooad.lms.model.SubmissionFileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionFileMetadataRepository extends JpaRepository<SubmissionFileMetadata, Long> {
    Optional<SubmissionFileMetadata> findBySubmissionIdAndStudentId(Long submissionId, Long studentId);

    Optional<SubmissionFileMetadata> findBySubmissionId(Long submissionId);

    boolean existsBySubmissionId(Long submissionId);
}
