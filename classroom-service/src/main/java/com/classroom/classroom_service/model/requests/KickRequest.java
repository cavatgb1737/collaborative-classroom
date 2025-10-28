package com.classroom.classroom_service.model.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class KickRequest {
    private UUID classroomId;
    private String email;
}
