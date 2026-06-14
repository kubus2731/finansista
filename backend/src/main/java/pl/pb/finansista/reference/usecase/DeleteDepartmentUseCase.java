package pl.pb.finansista.reference.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.Department;
import pl.pb.finansista.reference.exception.DepartmentNotFoundException;
import pl.pb.finansista.reference.repository.DepartmentRepository;

@Service
@RequiredArgsConstructor
public class DeleteDepartmentUseCase {

    private final DepartmentRepository departmentRepository;

    @Transactional
    public void execute(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(DepartmentNotFoundException::new);

        // A department still referenced by users/requests is FK-protected -> surfaces as 409.
        departmentRepository.delete(department);
    }
}
