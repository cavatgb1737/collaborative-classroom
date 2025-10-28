package com.classroom.stream_service.repository;

import com.classroom.stream_service.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    boolean existsByStudentEmailAndAssignmentId(String studentEmail, UUID assignmentId);
    List<Submission> findByAssignmentId(UUID assignmentId);

}
