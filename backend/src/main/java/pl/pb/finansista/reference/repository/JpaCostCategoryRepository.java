package pl.pb.finansista.reference.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.reference.CostCategory;

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
