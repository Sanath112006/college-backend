package com.college.complaintportal.repository;

import com.college.complaintportal.entity.ComplaintResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintResponseRepository extends JpaRepository<ComplaintResponse, Long> {

    List<ComplaintResponse> findByComplaintIdOrderByRespondedAtAsc(Long complaintId);
}
