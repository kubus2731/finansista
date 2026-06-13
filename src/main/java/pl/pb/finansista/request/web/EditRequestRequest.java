package pl.pb.finansista.request.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pl.pb.finansista.common.web.ExternalIdEncoder;
import pl.pb.finansista.request.usecase.EditRequestCommand;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record EditRequestRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull @Positive BigDecimal amount,
        String templateId,
        @NotNull Long departmentId,
        @NotNull Long costCategoryId,
        Long fundingSourceId
) {
    public EditRequestCommand toCommand(UUID externalId, String userEmail, List<String> userAuthorities, Long version) {
        return new EditRequestCommand(
                externalId,
                userEmail,
                userAuthorities,
                title,
                description,
                amount,
                templateId != null ? ExternalIdEncoder.decode(templateId) : null,
                departmentId,
                costCategoryId,
                fundingSourceId,
                version
        );
    }
}
