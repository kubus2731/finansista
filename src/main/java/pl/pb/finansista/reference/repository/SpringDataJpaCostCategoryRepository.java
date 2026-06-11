package pl.pb.finansista.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.reference.CostCategory;

interface SpringDataJpaCostCategoryRepository extends JpaRepository<CostCategory, Long> {
}
