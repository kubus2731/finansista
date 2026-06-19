package pl.pb.finansista.frontend.viewmodel;

import jakarta.validation.constraints.NotNull;

public record ChangeUserActiveRequest(@NotNull Boolean active) {}
