package com.ooad.lms.repository;

import com.ooad.lms.model.MaterialFileMetadata;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MaterialFileMetadataRepository {
    private final InMemoryDataStore dataStore;

    public MaterialFileMetadataRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Optional<MaterialFileMetadata> findById(Long id) {
        return Optional.ofNullable(dataStore.materialFileMetadata().get(id));
    }

    public MaterialFileMetadata save(MaterialFileMetadata metadata) {
        if (metadata.getFileId() == null) {
            throw new IllegalArgumentException("Material file ID must be set before saving metadata.");
        }
        dataStore.materialFileMetadata().put(metadata.getFileId(), metadata);
        dataStore.persist();
        return metadata;
    }

    public void deleteById(Long id) {
        dataStore.materialFileMetadata().remove(id);
        dataStore.persist();
    }
}