package pl.pb.finansista.request.usecase;

import java.util.Collection;
import java.util.UUID;

public record GetSingleRequestQuery(
        UUID externalId,
        String userEmail,
        Collection<String> userAuthorities
) {
}
