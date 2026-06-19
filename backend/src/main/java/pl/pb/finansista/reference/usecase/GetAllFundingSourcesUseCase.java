package pl.pb.finansista.reference.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.reference.FundingSource;
import pl.pb.finansista.reference.repository.FundingSourceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllFundingSourcesUseCase {

  private final FundingSourceRepository fundingSourceRepository;

  @Transactional(readOnly = true)
  public List<FundingSource> execute() {
    return fundingSourceRepository.findAll();
  }
}
