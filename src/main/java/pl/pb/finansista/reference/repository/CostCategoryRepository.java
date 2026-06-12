package pl.pb.finansista.reference.repository;

import pl.pb.finansista.reference.CostCategory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CostCategoryRepository {

    Optional<CostCategory> findById(Long id);

    CostCategory save(CostCategory costCategory);

    List<CostCategory> findAll();
}
