package pl.pb.finansista.frontend.viewmodel;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeUserRoleRequest(
        @NotNull Long roleId
) {
    
}

