package pl.pb.finansista.reference.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.Department;
import pl.pb.finansista.reference.exception.DepartmentAlreadyExistsException;
import pl.pb.finansista.reference.repository.DepartmentRepository;

@Service
@RequiredArgsConstructor
public class CreateDepartmentUseCase {

    private final DepartmentRepository departmentRepository;

    @Transactional
    public Department execute(String name) {
        if (departmentRepository.existsByName(name)) {
            throw new DepartmentAlreadyExistsException(name);
        }
        return departmentRepository.save(new Department(name));
    }
}
