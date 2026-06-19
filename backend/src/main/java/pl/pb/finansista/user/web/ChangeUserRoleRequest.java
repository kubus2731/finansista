package pl.pb.finansista.user.web;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import pl.pb.finansista.user.usecase.ChangeUserRoleCommand;

public record ChangeUserRoleRequest(@NotNull Long roleId) {
  public ChangeUserRoleCommand toCommand(UUID userExternalId) {
    return new ChangeUserRoleCommand(userExternalId, roleId);
  }
}
