package pl.pb.finansista.user.web;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import pl.pb.finansista.user.usecase.SetUserActiveCommand;

public record ChangeUserActiveRequest(@NotNull Boolean active) {
  public SetUserActiveCommand toCommand(UUID userExternalId, String actingUserEmail) {
    return new SetUserActiveCommand(userExternalId, active, actingUserEmail);
  }
}
