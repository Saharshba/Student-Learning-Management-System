package com.ooad.lms.dto;

import java.util.List;

import com.ooad.lms.model.MaterialComment;

public record MaterialCommentThreadResponse(
        Long courseId,
        String courseTitle,
        Long moduleId,
        String moduleTitle,
        Long materialId,
        String materialName,
        String contentUrl,
        List<MaterialComment> comments
) {
}