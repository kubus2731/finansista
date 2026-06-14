package pl.pb.finansista.request.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/** Section VI "Kwota Przyznana" — the dysponent's granted amount (may be a partial 0..requested). */
public record GrantFundingRequest(
        @NotNull @PositiveOrZero BigDecimal amountGranted
) {
}
