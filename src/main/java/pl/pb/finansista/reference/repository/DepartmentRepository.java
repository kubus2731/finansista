package pl.pb.finansista.reference.repository;

import pl.pb.finansista.reference.Department;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository {

    Optional<Department> findById(UUID id);

    Department save(Department department);
}
