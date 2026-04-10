package com.ooad.lms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "material_file_metadata")
public class MaterialFileMetadata {
    @Id
    private Long materialId;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private String originalFileName;

    protected MaterialFileMetadata() {
    }

    public MaterialFileMetadata(Long materialId, String storagePath, String originalFileName) {
        this.materialId = materialId;
        this.storagePath = storagePath;
        this.originalFileName = originalFileName;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }
}
