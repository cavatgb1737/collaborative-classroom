package com.classroom.stream_service.controller;

import com.classroom.stream_service.model.Assignment;
import com.classroom.stream_service.model.Post;
import com.classroom.stream_service.model.Submission;
import com.classroom.stream_service.model.requests.AssignmentRequest;
import com.classroom.stream_service.model.requests.PostRequest;
import com.classroom.stream_service.model.requests.SubmissionUpdate;
import com.classroom.stream_service.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stream")
@RequiredArgsConstructor
public class StreamController {

    private final StreamService streamService;

    // ---------------- POST ENDPOINTS ----------------

    @PostMapping("/assignments")
    public ResponseEntity<String> createAssignment(@RequestBody AssignmentRequest request) {
        streamService.postAssignment(request);
        return ResponseEntity.ok("Assignment successfully posted and event published.");
    }

    @PostMapping("/posts")
    public ResponseEntity<String> createPost(@RequestBody PostRequest request) {
        streamService.publishPost(request);
        return ResponseEntity.ok("Post successfully published to stream.");
    }

    @PostMapping("/assignments/turnin")
    public ResponseEntity<Assignment> turnInAssignment(
            @RequestPart("submission") Submission submission,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {
        Assignment updatedAssignment = streamService.turnInAssignment(submission, file);
        return ResponseEntity.ok(updatedAssignment);
    }

    @PutMapping("/submissions")
    public ResponseEntity<Submission> editSubmission(@RequestBody SubmissionUpdate submissionUpdate) {
        Submission updatedSubmission = streamService.editSubmission(submissionUpdate);
        return ResponseEntity.ok(updatedSubmission);
    }

    // ---------------- GET ENDPOINTS ----------------

    @GetMapping("/posts/{classroomId}")
    public ResponseEntity<List<Post>> getPosts(@PathVariable UUID classroomId) {
        return ResponseEntity.ok(streamService.getPostsByClassroomId(classroomId));
    }

    @GetMapping("/assignments/{classroomId}")
    public ResponseEntity<List<Assignment>> getAssignments(@PathVariable UUID classroomId) {
        return ResponseEntity.ok(streamService.getAssignmentsByClassroomId(classroomId));
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<List<Submission>> getSubmissions(@PathVariable UUID assignmentId) {
        return ResponseEntity.ok(streamService.seeStudentAssignmentSubmissions(assignmentId));
    }
}
