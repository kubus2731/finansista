package pl.pb.finansista.request.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.request.DepartmentRequestsSummary;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaDepartmentSummaryRepository implements DepartmentSummaryRepository {

    private final SpringDataJpaDepartmentSummaryRepository repository;

    @Override
    public List<DepartmentRequestsSummary> findAll() {
        return repository.findAll();
    }
}
