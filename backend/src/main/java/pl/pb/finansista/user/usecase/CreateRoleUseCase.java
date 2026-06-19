package pl.pb.finansista.user.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.user.Role;
import pl.pb.finansista.user.exception.RoleAlreadyExistsException;
import pl.pb.finansista.user.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class CreateRoleUseCase {

  private final RoleRepository roleRepository;

  @Transactional
  public Role execute(String name) {
    if (roleRepository.existsByName(name)) {
      throw new RoleAlreadyExistsException(name);
    }
    return roleRepository.save(new Role(name));
  }
}
