package pl.pb.finansista.user.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserByIdUseCase {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public User execute(UUID id) {
    return userRepository.findByExternalId(id).orElseThrow(UserNotFoundException::new);
  }
}
