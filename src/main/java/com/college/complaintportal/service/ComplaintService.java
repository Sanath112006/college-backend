package com.college.complaintportal.service;

import com.college.complaintportal.dto.*;
import com.college.complaintportal.entity.*;
import com.college.complaintportal.exception.ForbiddenException;
import com.college.complaintportal.exception.ResourceNotFoundException;
import com.college.complaintportal.repository.ComplaintRepository;
import com.college.complaintportal.repository.ComplaintResponseRepository;
import com.college.complaintportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintResponseRepository responseRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final FileStorageService fileStorageService;

    @Transactional
    public ComplaintResponseDto submitComplaint(ComplaintRequest request, MultipartFile file, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        String storedFileName = fileStorageService.storeFile(file);

        Complaint complaint = Complaint.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .locationType(request.getLocationType())
                .blockName(request.getBlockName())
                .roomNumber(request.getRoomNumber())
                .filePath(storedFileName)
                .isAnonymous(request.getIsAnonymous() != null && request.getIsAnonymous())
                .user(user)
                .build();
        complaint = complaintRepository.save(complaint);
        return mapToDto(complaint);
    }

    public ComplaintResponseDto getComplaintById(Long id, Long userId, boolean isAdmin) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", id));
        if (!isAdmin && !complaint.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied to this complaint");
        }
        return mapToDto(complaint);
    }

    public ComplaintResponseDto getComplaintByIdPublic(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", id));
        return mapToDto(complaint);
    }

    public List<ComplaintResponseDto> getMyComplaints(Long userId) {
        List<Complaint> complaints = complaintRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return complaints.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public Page<ComplaintResponseDto> getAllComplaints(Pageable pageable, ComplaintCategory category, ComplaintStatus status) {
        Page<Complaint> page;
        if (category != null && status != null) {
            page = complaintRepository.findByCategoryAndStatusOrderByCreatedAtDesc(category, status, pageable);
        } else if (category != null) {
            page = complaintRepository.findByCategoryOrderByCreatedAtDesc(category, pageable);
        } else if (status != null) {
            page = complaintRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else {
            page = complaintRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        return page.map(this::mapToDto);
    }

    @Transactional
    public ResponseDto addResponse(Long complaintId, ResponseRequest request, Long userId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", complaintId));
        ComplaintResponse response = ComplaintResponse.builder()
                .complaint(complaint)
                .message(request.getMessage())
                .build();
        response = responseRepository.save(response);
        
        // Send email notification for the new response
        mailService.sendResponseEmail(complaint, request.getMessage());
        
        return ResponseDto.builder()
                .id(response.getId())
                .complaintId(complaintId)
                .message(response.getMessage())
                .respondedAt(response.getRespondedAt())
                .build();
    }

    @Transactional
    public ComplaintResponseDto updateStatus(Long complaintId, StatusUpdateRequest request, Long userId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", complaintId));
        complaint.setStatus(request.getStatus());
        complaint = complaintRepository.save(complaint);
        
        // Send email notification for the status update
        mailService.sendStatusUpdateEmail(complaint);
        
        return mapToDto(complaint);
    }

    @Transactional
    public void deleteComplaint(Long id, Long userId, boolean isAdmin) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", id));
        if (!isAdmin && !complaint.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Only the owner or admin can delete this complaint");
        }
        complaintRepository.delete(complaint);
    }

    public java.nio.file.Path resolveFilePath(String storedFileName) {
        return fileStorageService.resolvePath(storedFileName);
    }

    private ComplaintResponseDto mapToDto(Complaint c) {
        String userName = c.getIsAnonymous() ? null : c.getUser().getName();
        List<ResponseDto> responses = c.getResponses().stream()
                .map(r -> ResponseDto.builder()
                        .id(r.getId())
                        .complaintId(c.getId())
                        .message(r.getMessage())
                        .respondedAt(r.getRespondedAt())
                        .build())
                .collect(Collectors.toList());
        return ComplaintResponseDto.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory())
                .locationType(c.getLocationType())
                .blockName(c.getBlockName())
                .roomNumber(c.getRoomNumber())
                .filePath(c.getFilePath())
                .status(c.getStatus())
                .isAnonymous(c.getIsAnonymous())
                .createdAt(c.getCreatedAt())
                .userId(c.getUser().getId())
                .userName(userName)
                .responses(responses)
                .build();
    }
}
