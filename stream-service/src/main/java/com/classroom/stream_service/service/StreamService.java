package com.classroom.stream_service.service;

import classroomservice.ClassroomServiceGrpc;
import classroomservice.ClassroomServiceOuterClass;
import com.classroom.stream_service.mapper.EventMapper;
import com.classroom.stream_service.model.*;
import com.classroom.stream_service.model.requests.AssignmentRequest;
import com.classroom.stream_service.model.requests.PostRequest;
import com.classroom.stream_service.model.requests.SubmissionUpdate;
import com.classroom.stream_service.repository.AssignmentRepository;
import com.classroom.stream_service.repository.PostRepository;
import com.classroom.stream_service.repository.SubmissionRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import userservice.UserServiceGrpc;
import userservice.UserServiceOuterClass;

import javax.annotation.PreDestroy;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StreamService {

    private final PostRepository postRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentEventProducer eventProducer;
    private final SubmissionRepository submissionRepository;
    private final S3Service s3Service;

    ManagedChannel classroom_channel = ManagedChannelBuilder
            .forAddress("localhost", 9091)
            .usePlaintext()
            .build();

    ManagedChannel user_channel = ManagedChannelBuilder
            .forAddress("localhost", 9093)
            .usePlaintext()
            .build();



    @PreDestroy
    public void shutdown(){
        user_channel.shutdown();
        classroom_channel.shutdown();
    }

    ClassroomServiceGrpc.ClassroomServiceBlockingStub classroom_stub =
            ClassroomServiceGrpc.newBlockingStub(classroom_channel);

    UserServiceGrpc.UserServiceBlockingStub user_stub =
            UserServiceGrpc.newBlockingStub(user_channel);

    private List<Assignment> buildAssignments(List<String> emails, AssignmentRequest request){
        return emails.stream()
                .map(email -> Assignment.builder()
                        .name(request.getName())
                        .docsLink(request.getDocsLink())
                        .classroomId(request.getClassroomId())
                        .dueDate(request.getDueDate())
                        .maxPoints(request.getMaxPoints())
                        .build())
                .toList();
    }


    public void postAssignment(AssignmentRequest request){

        ClassroomServiceOuterClass.ObtainEmailsRequest req = ClassroomServiceOuterClass.ObtainEmailsRequest.newBuilder()
                .setClassroomId(request.getClassroomId().toString())
                .build();

        ClassroomServiceOuterClass.ObtainEmailsResponse res = classroom_stub.retrieveEmails(req);

        if(!res.getPerms()){
            throw new AccessDeniedException("User does not have permission to post assignments.");
        }

        List<String> emails = res.getEmailsList();

        assignmentRepository.saveAll(buildAssignments(emails, request));

        AssignmentEvent event = EventMapper.toEvent(request, emails);

        eventProducer.publishAssignmentCreated(event);

    }

    public Assignment turnInAssignment(Submission submission, MultipartFile file) throws Exception {

        Assignment assignment = assignmentRepository.findById(submission.getAssignmentId())
                .orElseThrow(() -> new IllegalStateException("Assignment not found with id " + submission.getAssignmentId()));

        if(submissionRepository.existsByStudentEmailAndAssignmentId(submission.getStudentEmail(), submission.getAssignmentId())){
            throw new IllegalArgumentException("User has already submitted for this assignment.");
        }

        if(LocalDate.now().isAfter(assignment.getDueDate())){
            submission.setStatus(Submission.Status.LATE);
        }
        else{
            submission.setStatus(Submission.Status.SUBMITTED);
        }

        if(file != null){
            submission.setSubmittedFileUrl(s3Service.uploadFile(file));
        }

        submissionRepository.save(submission);

        return assignment;

    }

    public List<Submission> seeStudentAssignmentSubmissions(UUID assignmentId){
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    public Submission editSubmission(SubmissionUpdate submissionUpdate){

        Submission submission = submissionRepository.findById(submissionUpdate.getSubmissionId())
                .orElseThrow(() -> new IllegalStateException("Submission with id " + submissionUpdate.getSubmissionId() + " could not be found."));

        submission.setFeedback(submissionUpdate.getFeedback());
        submission.setGrade(submissionUpdate.getPoints());

        return submissionRepository.save(submission);
    }

    public void publishPost(PostRequest request){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        UserServiceOuterClass.RetrieveUserDataRes res = user_stub.retrieveUserData(
                UserServiceOuterClass.RetrieveUserDataReq.newBuilder()
                        .setEmail(email)
                        .build());

        Post post = Post.builder()
                .imageUrl(res.getImageUrl())
                .classroomId(request.getClassroomId())
                .username(res.getName())
                .content(request.getContent())
                .build();

        postRepository.save(post);

        PostEvent event = new PostEvent();
        event.setMessage("Stream has been updated, please refresh.");
        event.setClassroomId(request.getClassroomId());

        eventProducer.publishPostCreated(event);

    }

    public List<Post> getPostsByClassroomId(UUID classroomId){
        return postRepository.findByClassroomId(classroomId);
    }

    public List<Assignment> getAssignmentsByClassroomId(UUID classroomId){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return assignmentRepository.findByClassroomId(classroomId);

    }

}
