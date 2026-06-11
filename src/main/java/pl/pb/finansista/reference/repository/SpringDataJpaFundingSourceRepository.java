package pl.pb.finansista.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.reference.FundingSource;
import java.util.UUID;

public interface SpringDataJpaFundingSourceRepository extends JpaRepository<FundingSource, UUID> {
}
