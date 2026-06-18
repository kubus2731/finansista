package pl.pb.finansista.user.usecase;

import java.util.UUID;

public record SetUserActiveCommand(UUID userExternalId, boolean active, String actingUserEmail) {
}
