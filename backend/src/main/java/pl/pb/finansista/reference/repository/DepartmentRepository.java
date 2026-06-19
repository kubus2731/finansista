package pl.pb.finansista.reference.repository;

import java.util.List;
import java.util.Optional;
import pl.pb.finansista.reference.Department;

public interface DepartmentRepository {

  Optional<Department> findById(Long id);

  Department save(Department department);

  List<Department> findAll();

  void delete(Department department);

  boolean existsByName(String name);
}
