package pl.pb.finansista.user.repository;

import pl.pb.finansista.user.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {

    Optional<Role> findById(Long id);
    Optional<Role> findByName(String name);

    List<Role> findAll();
}

