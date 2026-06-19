package pl.pb.finansista.reference.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.Department;
import pl.pb.finansista.reference.repository.DepartmentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllDepartmentsUseCase {

  private final DepartmentRepository departmentRepository;

  @Transactional(readOnly = true)
  public List<Department> execute() {
    return departmentRepository.findAll();
  }
}
