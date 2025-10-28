package com.classroom.stream_service.model.requests;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class AssignmentRequest {

    private String name;

    private UUID classroomId;

    private String docsLink;

    private LocalDate dueDate;

    private int maxPoints;
}
