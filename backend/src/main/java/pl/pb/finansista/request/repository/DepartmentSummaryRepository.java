package pl.pb.finansista.request.repository;

import java.util.List;
import pl.pb.finansista.request.DepartmentRequestsSummary;

public interface DepartmentSummaryRepository {

  List<DepartmentRequestsSummary> findAll();
}
