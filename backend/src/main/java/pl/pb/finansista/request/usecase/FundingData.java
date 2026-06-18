package pl.pb.finansista.request.usecase;

import java.math.BigDecimal;

public record FundingData(Long fundingSourceId, BigDecimal amountRequested) {
}
