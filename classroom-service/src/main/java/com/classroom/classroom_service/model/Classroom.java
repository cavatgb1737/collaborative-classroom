package com.classroom.classroom_service.model;

import com.classroom.classroom_service.service.InviteCodeService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "classrooms")
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String classroomName;

    @Column(nullable = false, unique = true)
    private String inviteCode;

    private String description;

    @PrePersist
    public void inviteCode(){
        inviteCode = InviteCodeService.generateRandomCode();
    }

    private byte[] imageBytes;





}
