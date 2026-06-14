package pl.pb.finansista.request.usecase;

import java.math.BigDecimal;

/** Applicant input for one Section VI row: which source, and how much is requested. */
public record FundingData(Long fundingSourceId, BigDecimal amountRequested) {
}
