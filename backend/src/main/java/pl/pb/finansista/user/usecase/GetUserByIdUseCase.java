package pl.pb.finansista.user.usecase;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class GetUserByIdUseCase {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public User execute(UUID id) {
    return userRepository.findByExternalId(id).orElseThrow(UserNotFoundException::new);
  }
}
