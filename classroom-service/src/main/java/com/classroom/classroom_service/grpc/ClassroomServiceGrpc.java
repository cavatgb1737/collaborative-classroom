package com.classroom.classroom_service.grpc;

import classroomservice.ClassroomServiceOuterClass;
import com.classroom.classroom_service.model.Member;
import com.classroom.classroom_service.repository.MemberRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class ClassroomServiceGrpc extends classroomservice.ClassroomServiceGrpc.ClassroomServiceImplBase {

    private final MemberRepository memberRepository;

    @Override
    public void retrieveEmails(ClassroomServiceOuterClass.ObtainEmailsRequest request, StreamObserver<ClassroomServiceOuterClass.ObtainEmailsResponse> responseStreamObserver){

        UUID classroomId = UUID.fromString(request.getClassroomId());

        boolean perms = false;

        Optional<Member> teacherOpt = memberRepository.findById(UUID.fromString(request.getTeacherId()));


        if(teacherOpt.isPresent()){
            if(teacherOpt.get().getRole() == Member.Role.TEACHER){
                perms = true;
            }
        }

        List<Member> members = memberRepository.findByClassroomIdAndRole(classroomId, Member.Role.STUDENT);


        List<String> emails = new ArrayList<>();

        for(Member member: members){
            emails.add(member.getEmail());
        }

        ClassroomServiceOuterClass.ObtainEmailsResponse res = ClassroomServiceOuterClass.ObtainEmailsResponse.newBuilder()
                .addAllEmails(emails)
                .setPerms(perms)
                .build();

        responseStreamObserver.onNext(res);
        responseStreamObserver.onCompleted();

    }

}
