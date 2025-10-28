package com.classroom.stream_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "assignment_info")
@Entity
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private UUID classroomId;

    private String docsLink;

    private LocalDate postedAt;

    private LocalDate dueDate;

    private int maxPoints;

    @PrePersist
    public void postedAt(){
        postedAt = LocalDate.now();
    }




}
