package pl.pb.finansista.user.web;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import pl.pb.finansista.user.usecase.ChangeUserDepartmentCommand;

public record ChangeUserDepartmentRequest(@NotNull Long departmentId) {
  public ChangeUserDepartmentCommand toCommand(UUID userExternalId) {
    return new ChangeUserDepartmentCommand(userExternalId, departmentId);
  }
}
