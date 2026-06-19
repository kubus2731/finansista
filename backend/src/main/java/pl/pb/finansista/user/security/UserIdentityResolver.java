package pl.pb.finansista.user.security;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.common.security.Identity;
import pl.pb.finansista.common.security.IdentityResolver;
import pl.pb.finansista.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
class UserIdentityResolver implements IdentityResolver {

  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public Optional<Identity> resolve(UUID subject) {
    return userRepository
        .findByExternalId(subject)
        .map(user -> new Identity(user.getExternalId(), user.getRole().getName(), user.isActive()));
  }
}
