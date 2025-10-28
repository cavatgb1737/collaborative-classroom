package com.classroom.notifications_service.model;


import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
public class AssignmentEvent {

    private List<String> studentEmails;

    private String name;

    private UUID classroomId;

    private String docsLink;

    private LocalDate postedAt;

    private LocalDate dueDate;

}
