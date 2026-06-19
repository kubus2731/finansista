package pl.pb.finansista.request.repository;

import pl.pb.finansista.request.DepartmentRequestsSummary;

import java.util.List;

public interface DepartmentSummaryRepository {

  List<DepartmentRequestsSummary> findAll();
}
