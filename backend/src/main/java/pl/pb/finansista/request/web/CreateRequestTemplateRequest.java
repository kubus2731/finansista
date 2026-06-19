package pl.pb.finansista.request.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRequestTemplateRequest(
    @NotBlank @Size(max = 100) String title, @NotBlank String description) {}
