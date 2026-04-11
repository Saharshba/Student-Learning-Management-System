package com.ooad.lms.repository;

import com.ooad.lms.model.AssignmentFileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentFileMetadataRepository extends JpaRepository<AssignmentFileMetadata, Long> {
}
