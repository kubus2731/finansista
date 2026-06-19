package pl.pb.finansista.reference.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.CostCategory;
import pl.pb.finansista.reference.repository.CostCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllCostCategoriesUseCase {

  private final CostCategoryRepository costCategoryRepository;

  @Transactional(readOnly = true)
  public List<CostCategory> execute() {
    return costCategoryRepository.findAll();
  }
}
