package pl.pb.finansista.reference.repository;

import pl.pb.finansista.reference.Department;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository {

  Optional<Department> findById(Long id);

  Department save(Department department);

  List<Department> findAll();

  void delete(Department department);

  boolean existsByName(String name);
}
