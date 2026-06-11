package pl.pb.finansista.reference.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.reference.FundingSource;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
class JpaFundingSourceRepository implements FundingSourceRepository {

    private final SpringDataJpaFundingSourceRepository repository;

    @Override
    public Optional<FundingSource> findById(UUID id) {
        return repository.findById(id);
    }
}
