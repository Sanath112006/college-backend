package com.college.complaintportal.controller;

import com.college.complaintportal.dto.*;
import com.college.complaintportal.entity.ComplaintCategory;
import com.college.complaintportal.entity.ComplaintStatus;
import com.college.complaintportal.entity.User;
import com.college.complaintportal.entity.Role;
import com.college.complaintportal.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ComplaintResponseDto>> submitComplaint(
            @Valid @RequestPart("complaint") ComplaintRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal User user) {
        ComplaintResponseDto dto = complaintService.submitComplaint(request, file, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Complaint submitted successfully", dto));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ComplaintResponseDto>>> getMyComplaints(@AuthenticationPrincipal User user) {
        List<ComplaintResponseDto> complaints = complaintService.getMyComplaints(user.getId());
        return ResponseEntity.ok(ApiResponse.success(complaints));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComplaintResponseDto>> getComplaint(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        boolean isAdmin = user.getRole() == Role.ADMIN;
        ComplaintResponseDto dto = complaintService.getComplaintById(id, user.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> downloadComplaintFile(
            @PathVariable Long id) throws MalformedURLException {
        ComplaintResponseDto dto = complaintService.getComplaintByIdPublic(id);
        if (dto.getFilePath() == null || dto.getFilePath().isBlank()) {
            return ResponseEntity.notFound().build();
        }

        java.nio.file.Path filePath = complaintService.resolveFilePath(dto.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentDisposition = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ComplaintResponseDto>>> getAllComplaints(
            @RequestParam(required = false) ComplaintCategory category,
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal User user) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ComplaintResponseDto> result = complaintService.getAllComplaints(pageable, category, status);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/{id}/responses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ResponseDto>> addResponse(
            @PathVariable Long id,
            @Valid @RequestBody ResponseRequest request,
            @AuthenticationPrincipal User user) {
        ResponseDto dto = complaintService.addResponse(id, request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Response added", dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ComplaintResponseDto>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal User user) {
        ComplaintResponseDto dto = complaintService.updateStatus(id, request, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Status updated", dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComplaint(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        boolean isAdmin = user.getRole() == Role.ADMIN;
        complaintService.deleteComplaint(id, user.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Complaint deleted", null));
    }
}
