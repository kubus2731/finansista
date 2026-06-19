package pl.pb.finansista.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.user.Role;

interface SpringDataJpaRoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByName(String name);

  boolean existsByName(String name);
}
