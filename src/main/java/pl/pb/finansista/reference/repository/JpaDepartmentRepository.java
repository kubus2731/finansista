package pl.pb.finansista.reference.repository;

import org.springframework.stereotype.Repository;
import pl.pb.finansista.reference.Department;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaDepartmentRepository implements DepartmentRepository {

    private final SpringDataJpaDepartmentRepository repository;

    public JpaDepartmentRepository(SpringDataJpaDepartmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Department> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Department save(Department department) {
        return repository.save(department);
    }

    @Override
    public List<Department> findAll() {
        return repository.findAll();
    }
}
