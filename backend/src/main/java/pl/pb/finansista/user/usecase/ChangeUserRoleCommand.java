package pl.pb.finansista.user.usecase;

import java.util.UUID;

public record ChangeUserRoleCommand(
        UUID userExternalId,
        Long newRoleId
) {
}
