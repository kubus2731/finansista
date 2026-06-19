package pl.pb.finansista.frontend.viewmodel;

import jakarta.validation.constraints.NotBlank;

public record ChangeRequestStatusRequest(@NotBlank String status, String description) {}
