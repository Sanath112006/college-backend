package com.college.complaintportal.repository;

import com.college.complaintportal.entity.Complaint;
import com.college.complaintportal.entity.ComplaintCategory;
import com.college.complaintportal.entity.ComplaintStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Complaint> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Complaint> findByCategoryOrderByCreatedAtDesc(ComplaintCategory category, Pageable pageable);

    Page<Complaint> findByStatusOrderByCreatedAtDesc(ComplaintStatus status, Pageable pageable);

    Page<Complaint> findByCategoryAndStatusOrderByCreatedAtDesc(ComplaintCategory category, ComplaintStatus status, Pageable pageable);

    boolean existsByIdAndUserId(Long id, Long userId);
}
