package com.ooad.lms.repository;

import com.ooad.lms.model.AssignmentFileMetadata;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AssignmentFileMetadataRepository {
    private final InMemoryDataStore dataStore;

    public AssignmentFileMetadataRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Optional<AssignmentFileMetadata> findById(Long id) {
        return Optional.ofNullable(dataStore.assignmentFileMetadata().get(id));
    }

    public AssignmentFileMetadata save(AssignmentFileMetadata metadata) {
        dataStore.assignmentFileMetadata().put(metadata.getAssignmentId(), metadata);
        dataStore.persist();
        return metadata;
    }
}
