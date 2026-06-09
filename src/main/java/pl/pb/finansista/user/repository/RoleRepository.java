package pl.pb.finansista.user.repository;

import pl.pb.finansista.user.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {

    Optional<Role> findById(UUID id);
    Optional<Role> findByName(String name);
}

