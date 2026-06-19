package pl.pb.finansista.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.user.Role;

import java.util.Optional;

interface SpringDataJpaRoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByName(String name);

  boolean existsByName(String name);
}
