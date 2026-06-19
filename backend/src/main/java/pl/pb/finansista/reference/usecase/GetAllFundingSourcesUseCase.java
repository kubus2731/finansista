package pl.pb.finansista.reference.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.FundingSource;
import pl.pb.finansista.reference.repository.FundingSourceRepository;

@Service
@RequiredArgsConstructor
public class GetAllFundingSourcesUseCase {

  private final FundingSourceRepository fundingSourceRepository;

  @Transactional(readOnly = true)
  public List<FundingSource> execute() {
    return fundingSourceRepository.findAll();
  }
}
