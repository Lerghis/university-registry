package com.university.registry.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** The JSON a client sends to create a professor. Format rules only; business rules live in the model. */
public record ProfessorRequest(
        @NotBlank(message = Messages.REQUIRED) @Size(max = 20, message = Messages.TOO_LONG) String profId,
        @NotBlank(message = Messages.REQUIRED) @Size(max = 100, message = Messages.TOO_LONG) String specialty,
        @NotBlank(message = Messages.REQUIRED) @Size(max = 100, message = Messages.TOO_LONG) String name,
        @NotBlank(message = Messages.REQUIRED) @Size(max = 100, message = Messages.TOO_LONG) String email,
        @NotBlank(message = Messages.REQUIRED) @Size(max = 20, message = Messages.TOO_LONG) String phone)
{ }
