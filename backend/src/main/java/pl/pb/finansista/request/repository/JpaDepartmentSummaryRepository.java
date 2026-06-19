package pl.pb.finansista.request.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.request.DepartmentRequestsSummary;

@Repository
@RequiredArgsConstructor
public class JpaDepartmentSummaryRepository implements DepartmentSummaryRepository {

  private final SpringDataJpaDepartmentSummaryRepository repository;

  @Override
  public List<DepartmentRequestsSummary> findAll() {
    return repository.findAll();
  }
}
