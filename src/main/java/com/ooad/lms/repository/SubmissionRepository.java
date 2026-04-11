package com.ooad.lms.repository;

import com.ooad.lms.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
	List<Submission> findByStudentIdOrderByTimestampDesc(Long studentId);
}
