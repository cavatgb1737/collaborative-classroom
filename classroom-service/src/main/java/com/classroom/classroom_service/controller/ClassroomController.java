package com.classroom.classroom_service.controller;

import com.classroom.classroom_service.model.Classroom;
import com.classroom.classroom_service.model.requests.ClassroomRequest;
import com.classroom.classroom_service.model.requests.KickRequest;
import com.classroom.classroom_service.model.Member;
import com.classroom.classroom_service.service.ClassroomService;
import com.classroom.classroom_service.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;
    private final S3Service s3Service;


    @PostMapping
    public ResponseEntity<Classroom> createClassroom(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart ClassroomRequest req) throws Exception {

        return ResponseEntity.ok(classroomService.createClassroom(req, file));
    }

    @GetMapping
    public ResponseEntity<List<Classroom>> getClassrooms(){
        return ResponseEntity.ok(classroomService.getClassrooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable UUID id){
        return ResponseEntity.ok(classroomService.getClassroomById(id));
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<List<Member>> getMembersFromClassroom(@PathVariable UUID id){
        return ResponseEntity.ok(classroomService.getMembersFromClassroom(id));
    }

    @PostMapping("/join/{inviteCode}")
    public ResponseEntity<Void> joinClassroom(@PathVariable String inviteCode){
        classroomService.joinClassroom(inviteCode);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> kickMember(@RequestBody KickRequest req){
        classroomService.kickStudent(req.getClassroomId(), req.getEmail());
        return ResponseEntity.noContent().build();
    }
}
