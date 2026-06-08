package pl.pb.finansista.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>{
}
