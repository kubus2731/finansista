package pl.pb.finansista.user.web;

import jakarta.validation.constraints.NotBlank;

public record RoleRequest(@NotBlank String name) {}
