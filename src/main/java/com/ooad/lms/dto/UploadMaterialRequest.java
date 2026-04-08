package com.ooad.lms.dto;

import com.ooad.lms.model.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UploadMaterialRequest(
        @NotBlank String name,
        @NotNull MaterialType fileType,
        @NotBlank String contentUrl
) {
}