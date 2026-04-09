package com.ooad.lms.dto;

import com.ooad.lms.model.MaterialType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateExamRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String scheduleDateTime,
        @NotNull MaterialType fileType,
        @NotBlank String fileUrl
) {
}
