package com.ooad.lms.controller;

import com.ooad.lms.exception.NotFoundException;
import com.ooad.lms.model.MaterialFileMetadata;
import com.ooad.lms.repository.MaterialFileMetadataRepository;
import com.ooad.lms.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {
    private final MaterialFileMetadataRepository metadataRepository;
    private final FileStorageService fileStorageService;

    public MaterialController(MaterialFileMetadataRepository metadataRepository, FileStorageService fileStorageService) {
        this.metadataRepository = metadataRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/{materialId}/download")
    public ResponseEntity<Resource> downloadMaterial(@PathVariable Long materialId) {
        MaterialFileMetadata metadata = metadataRepository.findById(materialId)
                .orElseThrow(() -> new NotFoundException("Material PDF not found"));

        Resource resource = fileStorageService.loadAsResource(metadata.getStoragePath());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.inline().filename(metadata.getOriginalFileName()).build());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
