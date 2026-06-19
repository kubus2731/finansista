package pl.pb.finansista.reference.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.reference.FundingSource;

@Repository
@RequiredArgsConstructor
class JpaFundingSourceRepository implements FundingSourceRepository {

  private final SpringDataJpaFundingSourceRepository repository;

  @Override
  public Optional<FundingSource> findById(Long id) {
    return repository.findById(id);
  }

  @Override
  public FundingSource save(FundingSource fundingSource) {
    return repository.save(fundingSource);
  }

  @Override
  public List<FundingSource> findAll() {
    return repository.findAll();
  }
}
