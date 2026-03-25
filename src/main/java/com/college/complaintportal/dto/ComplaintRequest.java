package com.college.complaintportal.dto;

import com.college.complaintportal.entity.ComplaintCategory;
import com.college.complaintportal.entity.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    @NotNull(message = "Category is required")
    private ComplaintCategory category;

    @NotNull(message = "Location type (Class / Lab / Seminar Hall) is required")
    private LocationType locationType;

    @NotBlank(message = "Block name is required")
    @Size(max = 100)
    private String blockName;

    @Size(max = 50)
    private String roomNumber;

    @Builder.Default
    private Boolean isAnonymous = false;
}
