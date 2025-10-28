package com.classroom.stream_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "submissions")
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID assignmentId;

    private String studentEmail;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String docsLink;

    private String submittedFileUrl;

    private int grade;

    private String feedback;

    private LocalDateTime submittedAt;

    @PrePersist
    public void setDefaults(){
        submittedAt = LocalDateTime.now();
    }

    public enum Status{
        SUBMITTED,
        LATE
    }
}
