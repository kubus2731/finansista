package pl.pb.finansista.frontend.viewmodel;

import jakarta.validation.constraints.NotNull;

public record ChangeUserDepartmentRequest(@NotNull Long departmentId) {}
