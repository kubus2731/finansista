package pl.pb.finansista.common.security;

import java.util.Optional;
import java.util.UUID;

public interface IdentityResolver {

    Optional<Identity> resolve(UUID subject);
}
