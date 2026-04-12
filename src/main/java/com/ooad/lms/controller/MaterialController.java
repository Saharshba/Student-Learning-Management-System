package com.ooad.lms.controller;

import com.ooad.lms.dto.MaterialCommentRequest;
import com.ooad.lms.dto.MaterialReplyRequest;
import com.ooad.lms.exception.NotFoundException;
import com.ooad.lms.model.MaterialComment;
import com.ooad.lms.model.MaterialFileMetadata;
import com.ooad.lms.repository.MaterialFileMetadataRepository;
import com.ooad.lms.service.FileStorageService;
import com.ooad.lms.service.MaterialCommentService;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {
    private final MaterialFileMetadataRepository metadataRepository;
    private final FileStorageService fileStorageService;
    private final MaterialCommentService commentService;

    public MaterialController(MaterialFileMetadataRepository metadataRepository,
                              FileStorageService fileStorageService,
                              MaterialCommentService commentService) {
        this.metadataRepository = metadataRepository;
        this.fileStorageService = fileStorageService;
        this.commentService = commentService;
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

    @GetMapping("/{materialId}/comments")
    public java.util.List<MaterialComment> getComments(@PathVariable Long materialId) {
        return commentService.getComments(materialId);
    }

    @PostMapping("/{materialId}/comments")
    public MaterialComment postComment(
            @PathVariable Long materialId,
            @RequestParam Long studentId,
            @RequestBody MaterialCommentRequest request
    ) {
        return commentService.addComment(studentId, materialId, request);
    }

    @PostMapping("/{materialId}/comments/{commentId}/reply")
    public MaterialComment replyComment(
            @PathVariable Long materialId,
            @PathVariable Long commentId,
            @RequestParam Long instructorId,
            @RequestBody MaterialReplyRequest request
    ) {
        return commentService.addReply(instructorId, materialId, commentId, request);
    }
}
