package pl.pb.finansista.user.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.common.security.Identity;
import pl.pb.finansista.common.security.IdentityResolver;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class UserIdentityResolver implements IdentityResolver {

  private final UserRepository userRepository;

  // Runs from the security filter (no Open-Session-In-View yet), so the lazy role
  // would otherwise fail to initialize — the transaction keeps the session open
  // across the lookup and the getRole() access.
  @Override
  @Transactional(readOnly = true)
  public Optional<Identity> resolve(UUID subject) {
    return userRepository
        .findByExternalId(subject)
        .map(user -> new Identity(user.getExternalId(), user.getRole().getName(), user.isActive()));
  }
}
