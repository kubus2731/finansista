package pl.pb.finansista.user.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.user.Role;
import pl.pb.finansista.user.repository.RoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllRolesUseCase {

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<Role> execute() {
        return roleRepository.findAll();
    }
}
