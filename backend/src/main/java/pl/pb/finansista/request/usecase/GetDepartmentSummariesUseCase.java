package pl.pb.finansista.request.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.DepartmentRequestsSummary;
import pl.pb.finansista.request.repository.DepartmentSummaryRepository;

@Service
@RequiredArgsConstructor
public class GetDepartmentSummariesUseCase {

  private final DepartmentSummaryRepository repository;

  @Transactional(readOnly = true)
  public List<DepartmentRequestsSummary> execute() {
    return repository.findAll();
  }
}
