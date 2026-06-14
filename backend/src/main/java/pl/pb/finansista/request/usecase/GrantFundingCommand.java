package pl.pb.finansista.request.usecase;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

public record GrantFundingCommand(
        UUID requestExternalId,
        Long fundingSourceId,
        BigDecimal amountGranted,
        String userEmail,
        Collection<String> userAuthorities
) {
}
