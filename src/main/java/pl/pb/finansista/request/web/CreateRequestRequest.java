package pl.pb.finansista.request.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pl.pb.finansista.request.usecase.CreateRequestCommand;

import java.math.BigDecimal;

public record CreateRequestRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull @Positive BigDecimal amount,
        Long templateId,
        @NotNull Long departmentId,
        @NotNull Long costCategoryId,
        Long fundingSourceId
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
