package com.classroom.stream_service.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssignmentEvent {

    private List<String> studentEmails;

    private String name;

    private UUID classroomId;

    private String docsLink;

    private LocalDate postedAt;

    private LocalDate dueDate;


}
