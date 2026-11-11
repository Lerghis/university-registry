package com.university.registry.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GradeRequest(
        @NotBlank(message = Messages.REQUIRED) @Size(max = 20, message = Messages.TOO_LONG) String courseId,
        @NotNull(message = Messages.REQUIRED) Float value)
{ }
