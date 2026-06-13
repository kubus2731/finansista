package pl.pb.finansista.request.usecase;

import java.util.UUID;
import java.util.List;

public record ChangeRequestStatusCommand(
        UUID externalId,
        String newStatusName,
        String description,
        String userEmail,
        List<String> userAuthorities,
        Long version
) {
}
