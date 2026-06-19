package pl.pb.finansista.user.repository;

import java.util.List;
import java.util.Optional;
import pl.pb.finansista.user.Role;

public interface RoleRepository {

  Optional<Role> findById(Long id);

  Optional<Role> findByName(String name);

  List<Role> findAll();

  boolean existsByName(String name);

  Role save(Role role);

  void delete(Role role);
}
