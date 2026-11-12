package com.university.registry.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** The JSON a client sends to create a course. Format rules only; business rules live in the model. */
public record CourseRequest(
        @NotBlank(message = Messages.REQUIRED) @Size(max = 20, message = Messages.TOO_LONG) String courseId,
        @NotBlank(message = Messages.REQUIRED) @Size(max = 150, message = Messages.TOO_LONG) String courseTitle,
        int courseSemester)
{ }
