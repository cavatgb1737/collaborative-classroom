package com.classroom.stream_service.model;

import lombok.Data;

import java.util.UUID;

@Data
public class PostEvent {
    private String message;
    private UUID classroomId;
}
