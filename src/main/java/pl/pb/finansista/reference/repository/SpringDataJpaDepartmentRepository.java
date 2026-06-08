package pl.pb.finansista.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.reference.Department;
import java.util.UUID;

interface SpringDataJpaDepartmentRepository extends JpaRepository<Department, UUID> {
}
