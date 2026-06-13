package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.DepartmentRequestsSummary;

public interface SpringDataJpaDepartmentSummaryRepository extends JpaRepository<DepartmentRequestsSummary, Long> {
}
