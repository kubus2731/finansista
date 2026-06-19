package pl.pb.finansista.reference.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.Department;
import pl.pb.finansista.reference.repository.DepartmentRepository;

@Service
@RequiredArgsConstructor
public class GetAllDepartmentsUseCase {

  private final DepartmentRepository departmentRepository;

  @Transactional(readOnly = true)
  public List<Department> execute() {
    return departmentRepository.findAll();
  }
}
