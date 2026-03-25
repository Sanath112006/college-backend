package com.college.complaintportal.dto;

import com.college.complaintportal.entity.ComplaintCategory;
import com.college.complaintportal.entity.ComplaintStatus;
import com.college.complaintportal.entity.LocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponseDto {

    private Long id;
    private String title;
    private String description;
    private ComplaintCategory category;
    private LocationType locationType;
    private String blockName;
    private String roomNumber;
    private String filePath;
    private ComplaintStatus status;
    private Boolean isAnonymous;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;  // null if anonymous
    private List<ResponseDto> responses;
}
