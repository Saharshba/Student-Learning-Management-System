package com.ooad.lms.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCourseRequest(
        @NotBlank String title,
        @NotBlank String description
) {
}