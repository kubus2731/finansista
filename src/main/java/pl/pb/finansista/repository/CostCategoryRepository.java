package pl.pb.finansista.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.model.CostCategory;

@Repository
public interface CostCategoryRepository extends JpaRepository<CostCategory, Long>{
}
