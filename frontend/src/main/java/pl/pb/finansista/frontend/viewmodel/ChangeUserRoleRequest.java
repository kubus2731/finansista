package pl.pb.finansista.frontend.viewmodel;

import jakarta.validation.constraints.NotNull;

public record ChangeUserRoleRequest(@NotNull Long roleId) {}
