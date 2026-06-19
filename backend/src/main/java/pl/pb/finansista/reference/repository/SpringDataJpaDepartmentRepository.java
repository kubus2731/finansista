package pl.pb.finansista.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.reference.Department;

interface SpringDataJpaDepartmentRepository extends JpaRepository<Department, Long> {

  boolean existsByName(String name);
}
