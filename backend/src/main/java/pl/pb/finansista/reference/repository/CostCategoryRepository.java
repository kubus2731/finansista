package pl.pb.finansista.reference.repository;

import java.util.List;
import java.util.Optional;
import pl.pb.finansista.reference.CostCategory;

public interface CostCategoryRepository {

  Optional<CostCategory> findById(Long id);

  CostCategory save(CostCategory costCategory);

  List<CostCategory> findAll();
}
