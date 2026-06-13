package pl.pb.finansista.user.web;

import jakarta.validation.constraints.NotNull;
import pl.pb.finansista.user.usecase.ChangeUserRoleCommand;

import java.util.UUID;

public record ChangeUserRoleRequest(
        @NotNull Long roleId
) {
    public ChangeUserRoleCommand toCommand(UUID userExternalId) {
        return new ChangeUserRoleCommand(userExternalId, roleId);
    }
}
