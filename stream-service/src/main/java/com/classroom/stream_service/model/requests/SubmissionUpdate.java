package com.classroom.stream_service.model.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class SubmissionUpdate {
    private int points;
    private String feedback;
    private UUID submissionId;
}
