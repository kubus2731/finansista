package pl.pb.finansista.request.usecase;

import java.math.BigDecimal;
import java.util.UUID;

public record EditRequestCommand(
        UUID externalId,
        String userEmail,
        String title,
        String description,
        BigDecimal amount,
        Long templateId,
        Long departmentId,
        Long costCategoryId,
        Long fundingSourceId
) {
}
