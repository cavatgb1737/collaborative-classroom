package com.classroom.stream_service.model.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class PostRequest {

    private UUID classroomId;

    private String content;

}
