package pl.pb.finansista.reference.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.Department;
import pl.pb.finansista.reference.exception.DepartmentAlreadyExistsException;
import pl.pb.finansista.reference.exception.DepartmentNotFoundException;
import pl.pb.finansista.reference.repository.DepartmentRepository;

@Service
@RequiredArgsConstructor
public class EditDepartmentUseCase {

  private final DepartmentRepository departmentRepository;

  @Transactional
  public Department execute(Long id, String name) {
    Department department =
        departmentRepository.findById(id).orElseThrow(DepartmentNotFoundException::new);

    if (!department.getName().equals(name) && departmentRepository.existsByName(name)) {
      throw new DepartmentAlreadyExistsException(name);
    }

    department.rename(name);
    return departmentRepository.save(department);
  }
}
