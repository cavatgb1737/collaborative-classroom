package com.classroom.classroom_service.repository;

import com.classroom.classroom_service.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    boolean existsByEmailAndClassroomId(String email, UUID classroomId);
    Optional<Member> findByEmailAndClassroomId(String email, UUID classroomId);
    List<Member> findByEmail(String email);
    List<Member> findByClassroomId(UUID classroomId);
    List<Member> findByClassroomIdAndRole(UUID classroomId, Member.Role role);
}
