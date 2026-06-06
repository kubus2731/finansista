package pl.pb.finansista.user.repository;

import pl.pb.finansista.user.Role;

import java.util.Optional;

public interface RoleRepository {

    Optional<Role> findByName(String name);
}

