package pl.pb.finansista.user.web;

import jakarta.validation.constraints.NotNull;
import pl.pb.finansista.user.usecase.SetUserActiveCommand;

import java.util.UUID;

public record ChangeUserActiveRequest(
        @NotNull Boolean active
) {
    public SetUserActiveCommand toCommand(UUID userExternalId, String actingUserEmail) {
        return new SetUserActiveCommand(userExternalId, active, actingUserEmail);
    }
}
