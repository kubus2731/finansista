package pl.pb.finansista.user.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.DepartmentNotFoundException;
import pl.pb.finansista.reference.repository.DepartmentRepository;
import pl.pb.finansista.user.Role;
import pl.pb.finansista.user.RoleNotFoundException;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.UserAlreadyExistsException;
import pl.pb.finansista.user.repository.RoleRepository;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User execute(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        Role role = roleRepository.findById(command.roleId())
                .orElseThrow(RoleNotFoundException::new);

        var department = departmentRepository.findById(command.departmentId())
                .orElseThrow(DepartmentNotFoundException::new);

        String hashedPassword = passwordEncoder.encode(command.rawPassword());

        User newUser = new User(command.name(),
                command.surname(),
                command.email(),
                command.phoneNumber(),
                hashedPassword,
                role,
                department
        );

        userRepository.save(newUser);

        return newUser;
    }
}