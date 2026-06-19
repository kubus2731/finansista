package pl.pb.finansista.user.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.user.User;

import java.util.Optional;

interface SpringDataJpaUserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  Optional<User> findByExternalId(UUID externalId);

  boolean existsByEmail(String email);
}
