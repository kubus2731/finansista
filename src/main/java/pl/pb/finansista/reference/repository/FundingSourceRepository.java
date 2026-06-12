package pl.pb.finansista.reference.repository;

import pl.pb.finansista.reference.FundingSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FundingSourceRepository {
    Optional<FundingSource> findById(Long id);

    FundingSource save(FundingSource fundingSource);

    List<FundingSource> findAll();
}
