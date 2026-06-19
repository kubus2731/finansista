package pl.pb.finansista.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import pl.pb.finansista.user.User;

public interface UserRepository {

    Optional<User> findById(Long id);

    Optional<User> findByExternalId(UUID externalId);

    Optional<User> findByEmail(String email);

    User save(User user);

    List<User> findAll();

    boolean existsByEmail(String email);
}
