package pl.pb.finansista.user.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.user.Role;
import pl.pb.finansista.user.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class GetAllRolesUseCase {

  private final RoleRepository roleRepository;

  @Transactional(readOnly = true)
  public List<Role> execute() {
    return roleRepository.findAll();
  }
}
