package com.ooad.lms.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateModuleRequest(@NotBlank String title) {
}