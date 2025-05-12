package com.reliefcircle.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {
    @Min(value = 0, message = "Page number must be greater than or equal to 0")
    @Builder.Default
    private int pageNumber = 0;

    @Min(value = 1, message = "Page size must be greater than or equal to 1")
    @Max(value = 100, message = "Page size must be less than or equal to 100")
    @Builder.Default
    private int pageSize = 10;

    @Pattern(regexp = "^(asc|desc)$", message = "Sort direction must be either 'asc' or 'desc'")
    @Builder.Default
    private String sortDirection = "asc";

    private String sortBy;
} 