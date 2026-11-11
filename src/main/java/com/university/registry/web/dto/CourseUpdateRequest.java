package com.university.registry.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseUpdateRequest(
        @NotBlank(message = Messages.REQUIRED) @Size(max = 150, message = Messages.TOO_LONG) String courseTitle,
        int courseSemester)
{ }
