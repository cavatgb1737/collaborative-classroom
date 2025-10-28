package com.classroom.classroom_service.service;
import com.classroom.classroom_service.model.Classroom;
import com.classroom.classroom_service.model.requests.ClassroomRequest;
import com.classroom.classroom_service.model.Member;
import com.classroom.classroom_service.repository.ClassroomRepository;
import com.classroom.classroom_service.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;


    @Transactional
    public Classroom createClassroom(ClassroomRequest req, MultipartFile file) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();


        if(auth == null || !auth.isAuthenticated()){
            throw new IllegalStateException("No user is authenticated.");
        }

        Classroom classroom = Classroom.builder()
                .classroomName(req.getClassroomName())
                .description(req.getDescription())
                .build();

        if(file != null){
            classroom = s3Service.uploadFile(file, classroom);
        }


        classroomRepository.save(classroom);

        Member member = Member.builder()
                .classroomId(classroom.getId())
                .email(auth.getName())
                .role(Member.Role.TEACHER)
                .build();

        memberRepository.save(member);

        return classroom;
    }

    @Transactional
    public void joinClassroom(String inviteCode){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Classroom classroom = classroomRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new NoSuchElementException("No classroom found with invite code " + inviteCode));

        if(memberRepository.existsByEmailAndClassroomId(auth.getName(), classroom.getId())){
            throw new IllegalStateException("User is already in this classroom");
        }

        Member member = Member.builder()
                .classroomId(classroom.getId())
                .email(auth.getName())
                .role(Member.Role.STUDENT)
                .build();

        memberRepository.save(member);

    }

    private boolean hasTeacherPermissions(Authentication auth, UUID classroomId){
        return memberRepository.findByEmailAndClassroomId(auth.getName(), classroomId)
                .map(member -> member.getRole().equals(Member.Role.TEACHER))
                .orElse(false);
    }

    public List<Classroom> getClassrooms(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        List<Member> userMembers = memberRepository.findByEmail(email);

        List<Classroom> classrooms = new ArrayList<>();

        for(Member member: userMembers){
            classrooms.add(classroomRepository.findById(member.getClassroomId())
                    .orElseThrow(() -> new RuntimeException("Classroom does not exist with id " + member.getClassroomId())));
        }

        return classrooms;

    }

    @Transactional
    public void kickStudent(UUID classroomId, String memberEmail){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!hasTeacherPermissions(auth, classroomId)){
            throw new AccessDeniedException("Only teachers can kick others.");
        }

        Member memberToKick = memberRepository.findByEmailAndClassroomId(memberEmail, classroomId)
                .orElseThrow(() -> new IllegalStateException("Kicked user does not exist."));

        memberRepository.delete(memberToKick);

    }

    public Classroom getClassroomById(UUID id){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();


        if(!memberRepository.existsByEmailAndClassroomId(email, id)){
            throw new AccessDeniedException("User is not in this classroom");
        }

        return classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id " + id));

    }

    public List<Member> getMembersFromClassroom(UUID id){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!memberRepository.existsByEmailAndClassroomId(email, id)){
            throw new AccessDeniedException("User is not in this classroom");
        }

        return memberRepository.findByClassroomId(id);

    }

}
