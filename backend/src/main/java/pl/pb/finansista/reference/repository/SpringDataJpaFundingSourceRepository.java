package pl.pb.finansista.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.reference.FundingSource;

public interface SpringDataJpaFundingSourceRepository extends JpaRepository<FundingSource, Long> {}
