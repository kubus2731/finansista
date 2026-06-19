package pl.pb.finansista.user.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.user.Role;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.exception.RoleNotFoundException;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.RoleRepository;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChangeUserRoleUseCase {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  @Transactional
  public void execute(ChangeUserRoleCommand command) {
    User user =
        userRepository
            .findByExternalId(command.userExternalId())
            .orElseThrow(UserNotFoundException::new);

    Role newRole =
        roleRepository.findById(command.newRoleId()).orElseThrow(RoleNotFoundException::new);

    user.changeRole(newRole);
    userRepository.save(user);
  }
}
