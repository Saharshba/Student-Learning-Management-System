package com.ooad.lms.dto;

import jakarta.validation.constraints.NotBlank;

public record MaterialReplyRequest(
        @NotBlank String reply
) {
}
