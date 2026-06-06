package pl.pb.finansista.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.user.Role;

import java.util.Optional;
import java.util.UUID;

interface SpringDataJpaRoleRepository extends JpaRepository<Role, UUID> {
    
    Optional<Role> findByName(String name);
}

