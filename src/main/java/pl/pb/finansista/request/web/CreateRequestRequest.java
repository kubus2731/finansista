package pl.pb.finansista.request.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pl.pb.finansista.request.usecase.CreateRequestCommand;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateRequestRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull @Positive BigDecimal amount,
        UUID templateId,
        @NotNull UUID departmentId,
        @NotNull UUID costCategoryId,
        UUID fundingSourceId
) {
    public CreateRequestCommand toCommand(String userEmail) {
        return new CreateRequestCommand(
                title,
                description,
                amount,
                userEmail,
                templateId,
                departmentId,
                costCategoryId,
                fundingSourceId
        );
    }
}
