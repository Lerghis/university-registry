package com.university.registry.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseRequest(
        @NotBlank(message = Messages.REQUIRED) @Size(max = 20, message = Messages.TOO_LONG) String courseId,
        @NotBlank(message = Messages.REQUIRED) @Size(max = 150, message = Messages.TOO_LONG) String courseTitle,
        int courseSemester)
{ }
