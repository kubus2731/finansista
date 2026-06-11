package pl.pb.finansista.user.repository;

import org.springframework.stereotype.Repository;
import pl.pb.finansista.user.Role;

import java.util.Optional;
import java.util.UUID;

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
}

