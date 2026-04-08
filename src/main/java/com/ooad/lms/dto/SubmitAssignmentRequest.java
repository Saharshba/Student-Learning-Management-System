package com.ooad.lms.dto;

import jakarta.validation.constraints.NotBlank;

public record SubmitAssignmentRequest(@NotBlank String content) {
}