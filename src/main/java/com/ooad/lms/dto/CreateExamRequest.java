package com.ooad.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateExamRequest(
    @NotBlank String title,
    @NotBlank String description,
    @NotBlank String scheduleDateTime,
    @NotBlank String fileType,
    @NotBlank String fileUrl
) {}