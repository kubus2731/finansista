package pl.pb.finansista.reference.repository;

import pl.pb.finansista.reference.CostCategory;
import java.util.Optional;
import java.util.UUID;

public interface CostCategoryRepository {

    Optional<CostCategory> findById(UUID id);

    CostCategory save(CostCategory costCategory);
}
