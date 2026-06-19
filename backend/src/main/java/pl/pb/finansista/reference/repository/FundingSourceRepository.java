package pl.pb.finansista.reference.repository;

import java.util.List;
import java.util.Optional;
import pl.pb.finansista.reference.FundingSource;

public interface FundingSourceRepository {

  Optional<FundingSource> findById(Long id);

  FundingSource save(FundingSource fundingSource);

  List<FundingSource> findAll();
}
