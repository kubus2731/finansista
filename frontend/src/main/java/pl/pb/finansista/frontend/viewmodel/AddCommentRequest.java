package pl.pb.finansista.frontend.viewmodel;

import jakarta.validation.constraints.NotBlank;

public record AddCommentRequest(@NotBlank String content) {}
