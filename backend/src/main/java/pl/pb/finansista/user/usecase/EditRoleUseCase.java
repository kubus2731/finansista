package pl.pb.finansista.user.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.user.Role;
import pl.pb.finansista.user.exception.RoleAlreadyExistsException;
import pl.pb.finansista.user.exception.RoleNotFoundException;
import pl.pb.finansista.user.exception.SystemRoleModificationException;
import pl.pb.finansista.user.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class EditRoleUseCase {

    private final RoleRepository roleRepository;

    @Transactional
    public Role execute(Long id, String name) {
        Role role = roleRepository.findById(id)
                .orElseThrow(RoleNotFoundException::new);

        if (role.isBuiltIn()) {
            throw new SystemRoleModificationException();
        }
        if (!role.getName().equals(name) && roleRepository.existsByName(name)) {
            throw new RoleAlreadyExistsException(name);
        }

        role.rename(name);
        return roleRepository.save(role);
    }
}
