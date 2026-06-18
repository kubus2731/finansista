package pl.pb.finansista.frontend.viewmodel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record GrantFundingRequest(
        @NotNull @PositiveOrZero BigDecimal amountGranted
) {
}

