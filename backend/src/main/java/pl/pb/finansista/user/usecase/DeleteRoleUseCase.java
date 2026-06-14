package pl.pb.finansista.user.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.user.Role;
import pl.pb.finansista.user.exception.RoleNotFoundException;
import pl.pb.finansista.user.exception.SystemRoleModificationException;
import pl.pb.finansista.user.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class DeleteRoleUseCase {

    private final RoleRepository roleRepository;

    @Transactional
    public void execute(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(RoleNotFoundException::new);

        if (role.isBuiltIn()) {
            throw new SystemRoleModificationException();
        }

        roleRepository.delete(role);
    }
}
