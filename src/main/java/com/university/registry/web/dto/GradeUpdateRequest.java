package com.university.registry.web.dto;

import jakarta.validation.constraints.NotNull;

public record GradeUpdateRequest(
        @NotNull(message = Messages.REQUIRED) Float value)
{ }
