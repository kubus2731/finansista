package pl.pb.finansista.reference.repository;

import org.springframework.stereotype.Repository;
import pl.pb.finansista.reference.CostCategory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaCostCategoryRepository implements CostCategoryRepository {

    private final SpringDataJpaCostCategoryRepository repository;

    public JpaCostCategoryRepository(SpringDataJpaCostCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<CostCategory> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public CostCategory save(CostCategory costCategory) {
        return repository.save(costCategory);
    }

    @Override
    public List<CostCategory> findAll() {
        return repository.findAll();
    }
}
