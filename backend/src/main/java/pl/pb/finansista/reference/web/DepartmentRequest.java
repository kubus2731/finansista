package pl.pb.finansista.reference.web;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(@NotBlank String name) {}
