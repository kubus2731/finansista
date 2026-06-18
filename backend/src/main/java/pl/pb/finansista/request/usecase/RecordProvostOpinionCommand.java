package pl.pb.finansista.request.usecase;

import java.util.Collection;
import java.util.UUID;

public record RecordProvostOpinionCommand(
        UUID requestExternalId,
        String opinion,
        UUID userExternalId,
        Collection<String> userAuthorities
) {
}
