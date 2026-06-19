package pl.pb.finansista.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.user.User;

@Repository
class JpaUserRepository implements UserRepository {

    private final SpringDataJpaUserRepository repository;

    public JpaUserRepository(SpringDataJpaUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        // saveAndFlush wymusza INSERT od razu, dzięki czemu Hibernate generuje
        // externalId (@UuidGenerator) i zwraca go w bycie — potrzebne np. przy
        // rejestracji, gdzie zaraz po zapisie tworzymy token JWT z externalId.
        return repository.saveAndFlush(user);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
