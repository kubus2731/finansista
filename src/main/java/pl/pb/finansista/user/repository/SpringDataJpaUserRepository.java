package pl.pb.finansista.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.user.User;

import java.util.Optional;
import java.util.UUID;

interface SpringDataJpaUserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
}
