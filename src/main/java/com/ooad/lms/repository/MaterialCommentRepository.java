package com.ooad.lms.repository;

import com.ooad.lms.model.MaterialComment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MaterialCommentRepository {
    private final InMemoryDataStore dataStore;

    public MaterialCommentRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<MaterialComment> findByMaterialId(Long materialId) {
        return dataStore.materialComments().values().stream()
                .filter(comment -> comment.getMaterialId().equals(materialId))
                .collect(Collectors.toList());
    }

    public Optional<MaterialComment> findById(Long commentId) {
        return Optional.ofNullable(dataStore.materialComments().get(commentId));
    }

    public MaterialComment save(MaterialComment comment) {
        if (comment.getCommentId() == null) {
            throw new IllegalArgumentException("Comment ID must be set before saving.");
        }
        dataStore.materialComments().put(comment.getCommentId(), comment);
        dataStore.persist();
        return comment;
    }
}
