package pl.pb.finansista.request.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record GrantFundingRequest(@NotNull @PositiveOrZero BigDecimal amountGranted) {}
