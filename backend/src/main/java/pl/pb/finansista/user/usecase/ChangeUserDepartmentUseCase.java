package pl.pb.finansista.user.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.Department;
import pl.pb.finansista.reference.DepartmentNotFoundException;
import pl.pb.finansista.reference.repository.DepartmentRepository;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChangeUserDepartmentUseCase {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public void execute(ChangeUserDepartmentCommand command) {
        User user = userRepository.findByExternalId(command.userExternalId())
                .orElseThrow(UserNotFoundException::new);

        Department newDepartment = departmentRepository.findById(command.newDepartmentId())
                .orElseThrow(DepartmentNotFoundException::new);

        user.changeDepartment(newDepartment);
        userRepository.save(user);
    }
}
