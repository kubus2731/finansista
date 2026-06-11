package pl.pb.finansista.request.usecase;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateRequestCommand(
        String title,
        String description,
        BigDecimal amount,
        String userEmail,
        UUID templateId,
        UUID departmentId,
        UUID costCategoryId,
        UUID fundingSourceId
) {
}
