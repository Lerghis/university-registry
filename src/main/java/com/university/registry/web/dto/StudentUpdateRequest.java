package com.university.registry.web.dto;

public record StudentUpdateRequest(int semester, String name, String email, String phone) { }
