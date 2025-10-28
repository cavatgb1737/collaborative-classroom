package com.classroom.stream_service.mapper;


import com.classroom.stream_service.model.Assignment;
import com.classroom.stream_service.model.AssignmentEvent;
import com.classroom.stream_service.model.requests.AssignmentRequest;

import java.util.List;

public class EventMapper {

    public static AssignmentEvent toEvent(AssignmentRequest assignment, List<String> emails){

        return AssignmentEvent.builder()
                .name(assignment.getName())
                .classroomId(assignment.getClassroomId())
                .docsLink(assignment.getDocsLink())
                .dueDate(assignment.getDueDate())
                .studentEmails(emails)
                .build();

    }

}
