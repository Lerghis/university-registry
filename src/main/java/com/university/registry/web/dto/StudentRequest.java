package com.university.registry.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentRequest(
        @NotBlank(message = Messages.REQUIRED) @Size(max = 20, message = Messages.TOO_LONG) String am, int semester,
        @NotBlank(message = Messages.REQUIRED) @Size(max = 100, message = Messages.TOO_LONG) String name,
        @NotBlank(message = Messages.REQUIRED) @Size(max = 100, message = Messages.TOO_LONG) String email,
        @NotBlank(message = Messages.REQUIRED) @Size(max = 20, message = Messages.TOO_LONG) String phone)
{ }
