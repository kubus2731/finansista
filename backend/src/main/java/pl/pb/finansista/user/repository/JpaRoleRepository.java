package pl.pb.finansista.user.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.user.Role;

@Repository
class JpaRoleRepository implements RoleRepository {

  private final SpringDataJpaRoleRepository repository;

  public JpaRoleRepository(SpringDataJpaRoleRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<Role> findById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Optional<Role> findByName(String name) {
    return repository.findByName(name);
  }

  @Override
  public List<Role> findAll() {
    return repository.findAll();
  }

  @Override
  public boolean existsByName(String name) {
    return repository.existsByName(name);
  }

  @Override
  public Role save(Role role) {
    return repository.save(role);
  }

  @Override
  public void delete(Role role) {
    repository.delete(role);
  }
}
