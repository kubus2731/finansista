package pl.pb.finansista.reference.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.CostCategory;
import pl.pb.finansista.reference.repository.CostCategoryRepository;

@Service
@RequiredArgsConstructor
public class GetAllCostCategoriesUseCase {

  private final CostCategoryRepository costCategoryRepository;

  @Transactional(readOnly = true)
  public List<CostCategory> execute() {
    return costCategoryRepository.findAll();
  }
}
