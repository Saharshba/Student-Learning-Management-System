package com.ooad.lms.dto;

import jakarta.validation.constraints.NotBlank;

public record MaterialCommentRequest(
        @NotBlank String message
) {
}
