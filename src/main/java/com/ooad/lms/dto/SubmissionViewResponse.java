package com.ooad.lms.dto;

import java.time.LocalDateTime;

public record SubmissionViewResponse(
        Long submissionId,
        Long studentId,
        Long assignmentId,
        LocalDateTime timestamp,
        Double grade,
        String content,
        String feedback,
        boolean hasPdf
) {
}
